package com.pinguela.ypc.rest.api.login;

import java.io.IOException;
import java.net.URI;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.apis.openid.OpenIdOAuth2AccessToken;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessTokenErrorResponse;
import com.github.scribejava.core.oauth.AccessTokenRequestParams;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.github.scribejava.core.pkce.PKCE;
import com.github.scribejava.core.pkce.PKCEService;
import com.pinguela.DataException;
import com.pinguela.ServiceException;
import com.pinguela.yourpc.config.ConfigManager;
import com.pinguela.yourpc.model.Customer;
import com.pinguela.yourpc.service.CustomerService;
import com.pinguela.yourpc.service.impl.CustomerServiceImpl;
import com.pinguela.ypc.rest.api.constants.Parameters;
import com.pinguela.ypc.rest.api.constants.Paths;
import com.pinguela.ypc.rest.api.constants.SessionType;
import com.pinguela.ypc.rest.api.cookies.OAuthFlowCookie;
import com.pinguela.ypc.rest.api.cookies.SessionCookieConfig;
import com.pinguela.ypc.rest.api.exception.InvalidTokenException;
import com.pinguela.ypc.rest.api.exception.ResourceException;
import com.pinguela.ypc.rest.api.exception.ValidationException;
import com.pinguela.ypc.rest.api.internal.AlgorithmFactory;
import com.pinguela.ypc.rest.api.model.OAuthRedirectData;
import com.pinguela.ypc.rest.api.model.Session;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriInfo;

public class OAuthManager {

	private static Logger logger = LogManager.getLogger(OAuthManager.class);

	private static final OAuthManager INSTANCE = new OAuthManager(); 

	private static final String SCOPES = ConfigManager.getParameter("oauth.scopes"); 

	private static final String CLIENT_ID = ConfigManager.getParameter("oauth.google.client_id");
	private static final String CLIENT_SECRET = ConfigManager.getParameter("oauth.google.client_secret");

	private static final Algorithm STATE_ALGORITHM = AlgorithmFactory.createAlgorithm();
	private static final JWTVerifier STATE_VERIFIER = JWT.require(STATE_ALGORITHM)
			.acceptLeeway(60)
			.build();

	private SecureRandom secureRandom = new SecureRandom();

	// Lazily initialised
	private OAuth20Service oauthService;

	private PKCEService pkceService = PKCEService.defaultInstance();
	private OpenIdTokenVerifier idTokenVerifier = OpenIdTokenVerifier.getInstance();

	private CustomerService customerService;

	private OAuthManager() {
		this.customerService = new CustomerServiceImpl();
	}

	public static OAuthManager getInstance() {
		return INSTANCE;
	}

	private OAuth20Service getOrBuildOAuthService(ContainerRequestContext context) {

		if (this.oauthService == null) {
			URI baseUri = context.getUriInfo().getBaseUri();
			URI callbackUri = baseUri.resolve(Paths.OAUTH_CALLBACK);

			oauthService = new ServiceBuilder(CLIENT_ID)
					.apiSecret(CLIENT_SECRET)
					.defaultScope(SCOPES)
					.callback(callbackUri.toString())
					.build(GoogleApi20.instance());
		}

		return this.oauthService;
	}

	private PKCE generatePkce() {

		byte[] bytes = new byte[32];
		secureRandom.nextBytes(bytes);

		PKCE pkce = pkceService.generatePKCE(bytes);
		pkce.getAuthorizationUrlParams();
		return pkce;
	}

	/**
	 * Generate the data to be included in the redirect response to the OAuth consent screen
	 * @param provider The OAuth provider name
	 * @param redirectTo The URL to pass to the callback in order to redirect the user after successful authentication
	 * @param requestContext The current request
	 * @return An object containing the redirect URL to the consent screen and the cookies to set in the response
	 */
	public OAuthRedirectData initAuthFlow(String provider, String redirectTo, ContainerRequestContext requestContext) {
		OAuth20Service oauthService = getOrBuildOAuthService(requestContext);
		UriInfo uriInfo = requestContext.getUriInfo();

		PKCE pkce = generatePkce();
		String nonce = UUID.randomUUID().toString();
		Date expiry = Date.from(Instant.now().plus(5, ChronoUnit.MINUTES));

		URI baseUri = uriInfo.getBaseUri();
		String finalRedirect = baseUri.resolve(redirectTo).toString();

		String state = JWT.create()
				.withSubject(nonce)
				.withExpiresAt(expiry)
				.sign(STATE_ALGORITHM);

		Map<String, String> additionalParams = new HashMap<>();
		additionalParams.put("access_type", "offline");
		additionalParams.put("prompt", "consent");

		String authUrl = oauthService.createAuthorizationUrlBuilder()
				.additionalParams(additionalParams)
				.state(state)
				.pkce(pkce)
				.build();

		OAuthRedirectData redirectData = new OAuthRedirectData(baseUri)
				.withCookie(OAuthFlowCookie.PROVIDER, provider)
				.withCookie(OAuthFlowCookie.REDIRECT_TO, finalRedirect.toString())
				.withCookie(OAuthFlowCookie.CODE_VERIFIER, pkce.getCodeVerifier())
				.withCookie(OAuthFlowCookie.NONCE, nonce)
				.withRedirectUrl(authUrl);

		return redirectData;
	}

	/**
	 * Request the refresh token for the user that initiated the OAuth flow, for later authentication. 
	 * @param requestContext The current request
	 * @return An object containing the redirect URL to the consent screen and the cookies to set in the response
	 * @throws ValidationException if any of the required cookies or parameters is missing or invalid
	 */
	public OAuthRedirectData handleCallback(ContainerRequestContext requestContext) throws ValidationException {
		UriInfo uriInfo = requestContext.getUriInfo();
		MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
		Map<String, Cookie> cookies = requestContext.getCookies();

		validateState(queryParams, cookies);

		// Get access token and process the data
		OpenIdOAuth2AccessToken accessToken = getAccessToken(queryParams, cookies);

		// Verify the token's authenticity
		String provider = requiredCookie(cookies, OAuthFlowCookie.PROVIDER.getName());
		DecodedJWT idToken = idTokenVerifier.verifyJwt(provider, accessToken.getOpenIdToken());

		Customer customer = findOrRegisterCustomer(idToken);
		Session session = new Session(customer, SessionType.OAUTH);
		
		session.setProperty("provider", provider);
		session.setProperty("refresh_token", accessToken.getRefreshToken());

		String redirectTo = requiredCookie(cookies, OAuthFlowCookie.REDIRECT_TO.getName());

		return new OAuthRedirectData(uriInfo.getBaseUri())
				.withCookie(SessionCookieConfig.getInstance(), session.encode(Duration.ofDays(14)))
				.withExpiredAuthFlowCookies()
				.withRedirectUrl(redirectTo);
	}

	private void validateState(MultivaluedMap<String, String> queryParams, Map<String, Cookie> cookies) throws ValidationException {
		String stateParam = requiredParameter(queryParams, Parameters.STATE);
		String nonce = requiredCookie(cookies, OAuthFlowCookie.NONCE.getName());

		DecodedJWT state;

		try {
			state = STATE_VERIFIER.verify(stateParam);
		} catch (JWTVerificationException e) {
			logger.warn(e);
			throw new InvalidTokenException(e);
		}

		if (!nonce.equals(state.getSubject())) {
			logger.warn("Nonces from state and cookie do not match.");
			throw new ValidationException();
		}
	}

	private Customer findOrRegisterCustomer(DecodedJWT idToken) {
		String email = idToken.getClaim("email").asString();

		Customer customer;
		try {
			customer = this.customerService.findByEmail(email);
		} catch (ServiceException | DataException e) {
			logger.error(e);
			throw new ResourceException(e);
		} 

		if (customer != null) {
			return customer;
		}

		customer = new Customer();
		customer.setEmail(email);
		customer.setFirstName(idToken.getClaim("given_name").asString());

		try {
			Integer id = this.customerService.register(customer);
			customer.setId(id);
			return customer;
		} catch (ServiceException | DataException e) {
			logger.error(e);
			throw new ResourceException(e);
		}
	}

	private OpenIdOAuth2AccessToken getAccessToken(MultivaluedMap<String, String> queryParams, Map<String, Cookie> cookies) throws ValidationException {
		String code = requiredParameter(queryParams, Parameters.CODE);
		String codeVerifier = requiredCookie(cookies, OAuthFlowCookie.CODE_VERIFIER.getName());

		AccessTokenRequestParams reqParams = new AccessTokenRequestParams(code)
				.pkceCodeVerifier(codeVerifier);

		try {
			return (OpenIdOAuth2AccessToken) oauthService.getAccessToken(reqParams);
		} catch (OAuth2AccessTokenErrorResponse e) {
			logger.warn(e);
			throw new ValidationException(e);
		} catch (IOException | InterruptedException | ExecutionException e) {
			logger.error(e);
			throw new ResourceException(e);
		}
	}

	/**
	 * Refresh the session, returning a short-lived authentication token for the user.
	 * @param cookies The cookies for the current request
	 * @return An encoded authentication token
	 * @throws ValidationException If the required cookie is absent or invalid
	 */
	public String getSession(Map<String, Cookie> cookies) throws ValidationException {
		
		String sessionCookie = requiredCookie(cookies, SessionCookieConfig.getInstance().getName());
		Session session = Session.decode(sessionCookie);
		
		String provider = session.getProperty("provider");
		String refreshToken = session.getProperty("refresh_token");
		OpenIdOAuth2AccessToken accessToken;
		
		try {
			accessToken = (OpenIdOAuth2AccessToken) oauthService.refreshAccessToken(refreshToken);
		} catch (OAuth2AccessTokenErrorResponse e) {
			logger.warn(e);
			throw new ValidationException(e);
		} catch (IOException | InterruptedException | ExecutionException e) {
			logger.error(e);
			throw new ResourceException(e);
		}
		
		DecodedJWT idToken = idTokenVerifier.verifyJwt(provider, accessToken.getOpenIdToken());
		Customer customer = findOrRegisterCustomer(idToken);
		
		Session newSession = new Session(customer, SessionType.OAUTH);
		return newSession.encode(Duration.ofMinutes(10));
	}

	private String requiredParameter(MultivaluedMap<String, String> parameters, String name) throws ValidationException {
		return Optional.ofNullable(parameters.getFirst(name))
				.orElseThrow(() -> new ValidationException(String.format("Missing required parameter %s.", name)));
	}

	private String requiredCookie(Map<String, Cookie> cookies, String name) throws ValidationException {
		return Optional.ofNullable(cookies.get(name))
				.orElseThrow(() -> new ValidationException(String.format("Missing required cookie %s.", name)))
				.getValue();
	}

}
