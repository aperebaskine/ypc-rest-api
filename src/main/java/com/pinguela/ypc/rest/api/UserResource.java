
package com.pinguela.ypc.rest.api;

import org.apache.commons.validator.GenericValidator;

import com.pinguela.InvalidLoginCredentialsException;
import com.pinguela.YPCException;
import com.pinguela.yourpc.model.Customer;
import com.pinguela.yourpc.service.CustomerService;
import com.pinguela.yourpc.service.impl.CustomerServiceImpl;
import com.pinguela.ypc.rest.api.json.param.ParameterProcessor;
import com.pinguela.ypc.rest.api.model.ErrorLog;
import com.pinguela.ypc.rest.api.model.Exists;
import com.pinguela.ypc.rest.api.model.SessionToken;
import com.pinguela.ypc.rest.api.util.AuthUtils;
import com.pinguela.ypc.rest.api.util.ResponseWrapper;
import com.pinguela.ypc.rest.api.util.TokenManager;
import com.pinguela.ypc.rest.api.validation.Validators;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.SecurityContext;

@Path("/user")
public class UserResource {

	private TokenManager tokenManager = TokenManager.getInstance();
	private CustomerService customerService;

	public UserResource() {
		this.customerService = new CustomerServiceImpl();
	}

	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	@Operation(
			method = "POST",
			operationId = "loginCustomer",
			description = "Authenticates the customer, returning their info.", 
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully logged in, returning session token",
							content = @Content(
									mediaType = "text/plain"
									)
							), 
					@ApiResponse(
							responseCode = "404",
							description = "Customer not found"
							),
					@ApiResponse(
							responseCode = "400",
							description = "Error in request"
							)
			})
	public Response login(
			@FormParam("email") @Email @NotNull String email,
			@FormParam("password") @NotNull String password
			) {

		Customer c;

		try {
			c = customerService.login(email, password);

		} catch (InvalidLoginCredentialsException e) {
			return Response.status(Status.NOT_FOUND).build();
		} catch (YPCException e) {
			return Response.status(Status.BAD_REQUEST).build();
		}

		return ResponseWrapper.wrap(() -> tokenManager.encodeToken(c));
	}

	@POST
	@Path("/register")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			method = "POST",
			operationId = "registerCustomer",
			description = "Create a customer account", 
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully registered.",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(implementation = SessionToken.class)
									)
							),
					@ApiResponse(
							responseCode = "400",
							description = "Error in request",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(implementation = ErrorLog.class)
									)
							)
			})
	public Response register(
			@FormParam("firstName") @NotNull String firstName,
			@FormParam("lastName1") @NotNull String lastName1,
			@FormParam("lastName2") String lastName2,
			@FormParam("docType") @NotNull String documentTypeId,
			@FormParam("docNumber") @NotNull String documentNumber,
			@FormParam("phoneNumber") @NotNull String phoneNumber,
			@FormParam("email") @NotNull String email,
			@FormParam("password") @NotNull String password
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
						throw new WebApplicationException(Status.BAD_REQUEST);
					}

					return tokenManager.encodeToken(createdCustomer);
				});
	}

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			method = "GET",
			operationId = "getAuthenticatedUser",
			description = "Retrieve user data from session token", 
			security = @SecurityRequirement(name = "bearerAuth"),
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully retrieved user data.",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(implementation = Customer.class)
									)
							),
					@ApiResponse(
							responseCode = "404",
							description = "No user associated with session token"
							)
			})
	public Response getAuthenticatedUser(
			@Context SecurityContext securityContext
			) {
		Integer id = AuthUtils.getUserId(securityContext);
		return ResponseWrapper.wrap(() -> customerService.findById(id), Status.NOT_FOUND);
	}

	@GET
	@Path("/exists")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			method = "GET",
			operationId = "exists",
			description = "Check whether an email and/or phone number is already in use", 
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully retrieved whether email or phone number exists",
							content = @Content(
									schema = @Schema(
											implementation = Exists.class
											),
									mediaType = MediaType.APPLICATION_JSON
									)
							),
					@ApiResponse(
							responseCode = "500",
							description = "Unknown error occured"
							)
			})
	public Response exists(
			@QueryParam("email") @Email String email,
			@QueryParam("phoneNumber") String phoneNumber
			) {
		return ResponseWrapper.wrap(() -> {
			return new Exists(
					GenericValidator.isBlankOrNull(email) ? null : this.customerService.emailExists(email),
							GenericValidator.isBlankOrNull(phoneNumber) ? null : this.customerService.phoneNumberExists(phoneNumber)
					);
		});
	}

}
