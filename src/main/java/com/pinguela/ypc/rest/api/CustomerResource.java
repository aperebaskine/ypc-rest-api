
package com.pinguela.ypc.rest.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pinguela.InvalidLoginCredentialsException;
import com.pinguela.YPCException;
import com.pinguela.yourpc.config.ConfigManager;
import com.pinguela.yourpc.model.Customer;
import com.pinguela.yourpc.service.CustomerService;
import com.pinguela.yourpc.service.impl.CustomerServiceImpl;
import com.pinguela.ypc.rest.api.annotations.Public;
import com.pinguela.ypc.rest.api.constants.Paths;
import com.pinguela.ypc.rest.api.constants.Roles;
import com.pinguela.ypc.rest.api.constants.SessionType;
import com.pinguela.ypc.rest.api.cookies.SessionCookieConfig;
import com.pinguela.ypc.rest.api.exception.ValidationException;
import com.pinguela.ypc.rest.api.json.param.ParameterProcessor;
import com.pinguela.ypc.rest.api.login.OAuthManager;
import com.pinguela.ypc.rest.api.model.CustomerDTOMixin;
import com.pinguela.ypc.rest.api.model.OAuthResponseData;
import com.pinguela.ypc.rest.api.model.Session;
import com.pinguela.ypc.rest.api.util.CookieUtils;
import com.pinguela.ypc.rest.api.util.ResponseWrapper;
import com.pinguela.ypc.rest.api.validation.Validators;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/")
@Tag(name = "customer")
@ApiResponses(
		@ApiResponse(
				responseCode = "400",
				description = "One or more of the request parameters is malformed"
				)
		)
public class CustomerResource {

	private static Logger logger = LogManager.getLogger(CustomerResource.class);

	private OAuthManager oauthManager = OAuthManager.getInstance();
	private CustomerService customerService;

	public CustomerResource() {
		this.customerService = new CustomerServiceImpl();
	}

	@POST
	@Public
	@Path("customers/login")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	@Operation(
			method = "POST",
			operationId = "loginCustomer",
			description = "Authenticates the customer, returning a short-lived JWT to be used as Bearer authorization header token.", 
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully logged in",
							content = @Content(
									mediaType = MediaType.TEXT_PLAIN,
									schema = @Schema(
											type = "string",
											format = "byte"
											)
									)
							),
					@ApiResponse(
							responseCode = "404",
							description = "Customer not found"
							)
			})
	public Response login(
			@FormParam("email") @Email String email,
			@FormParam("password") String password,
			@Context ContainerRequestContext context
			) {

		Customer customer;

		try {
			customer = customerService.login(email, password);

		} catch (InvalidLoginCredentialsException e) {
			return Response.status(Status.NOT_FOUND).build();
		} catch (YPCException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}

		return buildNewLoginResponse(context, customer);
	}

	@POST
	@Path("customers/logout")
	@Operation(
			method = "POST",
			operationId = "logoutCustomer",
			description = "Removes the user's session cookie, preventing them from refreshing the short-lived JWT without logging in again.",
			security = @SecurityRequirement(name = "bearerAuth"),
			responses = {
					@ApiResponse(
							responseCode = "204", 
							description = "Successfully logged out"
							),
					@ApiResponse(
							responseCode = "401",
							description = "Caller is unauthenticated"
							)
			})
	public Response logout(
			@Context ContainerRequestContext context
			) {
		return Response.status(Status.NO_CONTENT)
				.cookie(CookieUtils.expiredCookie(context, SessionCookieConfig.getInstance()))
				.build();
	}

	@POST
	@Public
	@Path("customers/register")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	@Operation(
			method = "POST",
			operationId = "registerCustomer",
			description = "Creates a customer account, returning a short-lived JWT to be used as Bearer authorization header token.", 
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully registered",
							content = @Content(
									mediaType = MediaType.TEXT_PLAIN,
									schema = @Schema(
											type = "string",
											format = "byte"
											)
									)
							)
			})
	public Response register(
			@FormParam("firstName") String firstName,
			@FormParam("lastName1") String lastName1,
			@FormParam("lastName2") String lastName2,
			@FormParam("docType") String documentTypeId,
			@FormParam("docNumber") String documentNumber,
			@FormParam("phoneNumber") String phoneNumber,
			@FormParam("email") @NotNull String email,
			@FormParam("password") @NotNull String password,
			@Context ContainerRequestContext context
			) {
		return new ParameterProcessor()
				.validate("email", email, Validators.isUnusedEmail())
				.buildResponse(() -> {
					Customer customer = new Customer();
					customer.setFirstName(firstName);
					customer.setLastName1(lastName1);
					customer.setLastName2(lastName2);
					customer.setDocumentTypeId(documentTypeId);
					customer.setDocumentNumber(documentNumber);
					customer.setPhoneNumber(phoneNumber);
					customer.setEmail(email);
					customer.setUnencryptedPassword(password);

					Integer id = customerService.register(customer);
					Customer createdCustomer = customerService.findById(id);

					if (createdCustomer == null) {
						throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
					}

					return buildNewLoginResponse(context, createdCustomer);
				});
	}

	private Response buildNewLoginResponse(ContainerRequestContext context, Customer customer) {
		Session session = new Session(customer, SessionType.CREDENTIALS);

		String bearerToken = session.encode(Duration.ofMinutes(10));

		SessionCookieConfig config = SessionCookieConfig.getInstance();
		Duration cookieDuration = Duration.ofSeconds(config.getMaxAge());
		NewCookie sessionCookie = CookieUtils.newCookie(context, config, session.encode(cookieDuration));

		return Response.ok(bearerToken)
				.cookie(sessionCookie)
				.build();
	}

	@POST
	@Public
	@Path(Paths.OAUTH)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	@Operation(
			method = "POST",
			operationId = "loginCustomerWithOAuth",
			description = "Initializes the OAuth code flow, returning the consent screen URL that the user should be redirected to.", 
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully initialized code flow, returned consent screen URL",
							content = @Content(
									mediaType = MediaType.TEXT_PLAIN,
									schema = @Schema(
											type = "string"
											)
									)
							)
			})
	public Response oauthAuthorize(
			@FormParam("provider") 
			@DefaultValue("google") 
			@Parameter(
					description = "Reserved for future use",
					hidden = true
					)
			String provider,
			@FormParam("redirectTo") @NotNull @DefaultValue("/") String redirectTo,
			@Context ContainerRequestContext requestContext
			) throws URISyntaxException {

		if (!isValidRedirectUri(redirectTo)) {
			throw new WebApplicationException(Status.BAD_REQUEST);
		}

		OAuthResponseData responseData = oauthManager.initAuthFlow("google", redirectTo, requestContext);

		return Response
				.ok(responseData.getUrl())
				.cookie((NewCookie[]) responseData.getCookies().toArray(new NewCookie[0]))
				.build();
	}

	@GET
	@Public
	@Hidden
	@Path(Paths.OAUTH_CALLBACK)
	public Response oauthCallback(
			@Context ContainerRequestContext context
			) {

		OAuthResponseData responseData;

		try {
			responseData = oauthManager.handleCallback(context);
		} catch (ValidationException e) {
			logger.warn(e);
			throw new WebApplicationException(Status.UNAUTHORIZED);
		}

		URI redirectUri = URI.create(responseData.getUrl());

		return Response
				.status(Status.FOUND)
				.location(redirectUri)
				.cookie(responseData.getCookies().toArray(new NewCookie[0]))
				.build();
	}

	private boolean isValidRedirectUri(String redirectUri) {
		if (ConfigManager.isDebug()) { // Allows localhost absolute URLs
			return true;
		}

		URI uri = URI.create(redirectUri);
		return !uri.isAbsolute(); // Disallow redirects to other domains
	}

	@GET
	@Path("customers/{customerId: \\d+}")
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({Roles.ADMIN, Roles.HR, Roles.SUPPORT})
	@Operation(
			method = "GET",
			operationId = "findCustomerById",
			description = "Retrieve customer data from their ID. Roles allowed: admin, hr, support", 
			security = @SecurityRequirement(name = "bearerAuth"),
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully retrieved customer data",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(implementation = CustomerDTOMixin.class)
									)
							),
					@ApiResponse(
							responseCode = "401",
							description = "Caller is unauthenticated"
							),
					@ApiResponse(
							responseCode = "404",
							description = "No customer found"
							)
			})
	public Response findById(
			@PathParam("customerId") Integer customerId
			) {
		return ResponseWrapper.wrap(() -> customerService.findById(customerId), Status.NOT_FOUND);
	}

	@HEAD
	@Public
	@Path("customers/{customerEmail: .+%40.+}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			method = "HEAD",
			operationId = "customerEmailExists",
			description = "Check whether an email is already in use by a customer", 
			responses = {
					@ApiResponse(
							responseCode = "204", 
							description = "Email already exists"
							),
					@ApiResponse(
							responseCode = "404",
							description = "Email doesn't exist"
							)
			})
	public Response exists(
			@PathParam("customerEmail") @Email @NotNull String customerEmail
			) {
		boolean exists;

		try {
			exists = customerService.emailExists(customerEmail);
		} catch (YPCException e) {
			logger.error(e);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}

		return Response.status(exists ? Status.NO_CONTENT : Status.NOT_FOUND).build();			
	}

	@GET
	@Path("customers/{customerEmail: .+%40.+}")
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({Roles.ADMIN, Roles.HR, Roles.SUPPORT})
	@Operation(
			method = "GET",
			operationId = "findCustomerByEmail",
			description = "Retrieve customer data from their email. Roles allowed: admin, hr, support", 
			security = @SecurityRequirement(name = "bearerAuth"),
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully retrieved customer data",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(implementation = Customer.class)
									)
							),
					@ApiResponse(
							responseCode = "401",
							description = "Caller is unauthenticated"
							),
					@ApiResponse(
							responseCode = "404",
							description = "No customer found"
							)
			})
	public Response findByEmail(
			@PathParam("customerEmail") @Email @NotNull String customerEmail
			) {
		return ResponseWrapper.wrap(() -> customerService.findByEmail(customerEmail), Status.NOT_FOUND);	
	}

}
