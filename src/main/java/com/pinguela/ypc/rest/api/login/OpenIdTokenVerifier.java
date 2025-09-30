package com.pinguela.ypc.rest.api.login;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.pinguela.yourpc.config.ConfigManager;
import com.pinguela.yourpc.service.cache.Cache;
import com.pinguela.yourpc.service.cache.CacheManager;
import com.pinguela.ypc.rest.api.exception.ValidationException;
import com.pinguela.ypc.rest.api.internal.AlgorithmFactory;

class OpenIdTokenVerifier {

	private static Logger logger = LogManager.getLogger(OpenIdTokenVerifier.class);

	private static final OpenIdTokenVerifier INSTANCE = new OpenIdTokenVerifier();

	private static final String ISSUER = "https://accounts.google.com";
	private static final String JWKS_URL = "https://www.googleapis.com/oauth2/v3/certs";
	private static final String AUDIENCE = ConfigManager.getParameter("oauth.google.client_id");

	private static final Cache<String, JWTVerifier> CACHE = CacheManager.getInstance().getCache("oidVerifier", String.class, JWTVerifier.class);

	private final JwkProvider jwkProvider;

	private OpenIdTokenVerifier() {
		
		URL url;
		
		try {
			url = new URL(JWKS_URL);
		} catch (MalformedURLException e) {
			logger.fatal(e);
			throw new ExceptionInInitializerError(e);
		}
		
		jwkProvider = new JwkProviderBuilder(url)
				.cached(64, 24, TimeUnit.HOURS)
				.build();
	}

	public static OpenIdTokenVerifier getInstance() {
		return INSTANCE;
	}

	private PublicKey getPublicKey(String provider, DecodedJWT jwt) throws ValidationException {
		String keyId = jwt.getKeyId();
		try {
			Jwk jwk = jwkProvider.get(keyId);
			return jwk.getPublicKey();
		} catch (JwkException e) {
			logger.error(e);
			throw new ValidationException(e);
		}
	}

	private JWTVerifier getJwtVerifier(RSAPublicKey key) {
		String keyStr = key.toString();
		return CACHE.computeIfAbsent(keyStr, k -> {
			Algorithm algorithm = AlgorithmFactory.createAlgorithm(key); 
			return JWT.require(algorithm)
					.withIssuer(ISSUER)
					.withAudience(AUDIENCE)
					.acceptLeeway(60)
					.build();
		});
	}

	public DecodedJWT verifyJwt(String provider, String jwt) throws ValidationException {
		DecodedJWT decodedJwt = JWT.decode(jwt); // Unverified
		RSAPublicKey publicKey = (RSAPublicKey) getPublicKey(provider, decodedJwt);

		JWTVerifier verifier = getJwtVerifier(publicKey);

		try {
			return verifier.verify(decodedJwt);
		} catch (JWTVerificationException e) {
			logger.warn(e);
			throw new ValidationException(e);
		}
	}

}
