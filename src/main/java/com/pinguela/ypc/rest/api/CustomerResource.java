
package com.pinguela.ypc.rest.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pinguela.InvalidLoginCredentialsException;
import com.pinguela.YPCException;
import com.pinguela.yourpc.model.Customer;
import com.pinguela.yourpc.service.CustomerService;
import com.pinguela.yourpc.service.impl.CustomerServiceImpl;
import com.pinguela.ypc.rest.api.annotations.Public;
import com.pinguela.ypc.rest.api.constants.Roles;
import com.pinguela.ypc.rest.api.json.param.ParameterProcessor;
import com.pinguela.ypc.rest.api.model.CustomerDTOMixin;
import com.pinguela.ypc.rest.api.model.ErrorLog;
import com.pinguela.ypc.rest.api.util.ResponseWrapper;
import com.pinguela.ypc.rest.api.util.TokenManager;
import com.pinguela.ypc.rest.api.validation.Validators;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/customer")
@Tag(name = "customer")
public class CustomerResource {

	private static Logger logger = LogManager.getLogger(CustomerResource.class);

	private TokenManager tokenManager = TokenManager.getInstance();
	private CustomerService customerService;

	public CustomerResource() {
		this.customerService = new CustomerServiceImpl();
	}

	@POST
	@Public
	@Path("/login")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(com.pinguela.ypc.rest.api.constants.MediaType.APPLICATION_JWT)
	@Operation(
			method = "POST",
			operationId = "loginCustomer",
			description = "Authenticates the customer, returning a JWT containing 'name', 'fullName' and 'role' claims", 
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully logged in",
							content = @Content(
									mediaType = com.pinguela.ypc.rest.api.constants.MediaType.APPLICATION_JWT,
									schema = @Schema(
											type = "string",
											format = "byte"
											)
									)
							),
					@ApiResponse(
							responseCode = "400",
							description = "Malformed request"
							),
					@ApiResponse(
							responseCode = "404",
							description = "Customer not found"
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
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}

		return ResponseWrapper.wrap(() -> tokenManager.encodeToken(c));
	}

	@POST
	@Public
	@Path("/register")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(com.pinguela.ypc.rest.api.constants.MediaType.APPLICATION_JWT)
	@Operation(
			method = "POST",
			operationId = "registerCustomer",
			description = "Creates a customer account, returning a JWT containing 'name', 'fullName' and 'role' claims", 
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully registered",
							content = @Content(
									mediaType = com.pinguela.ypc.rest.api.constants.MediaType.APPLICATION_JWT
									)
							),
					@ApiResponse(
							responseCode = "400",
							description = "Malformed request",
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
						throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
					}

					return tokenManager.encodeToken(createdCustomer);
				});
	}

	@GET
	@Path("/{customerId:^\\d+$}")
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
							responseCode = "400",
							description = "Malformed request"
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
	@Path("/{customerEmail}")
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
							responseCode = "400",
							description = "Email parameter is malformed"
							),
					@ApiResponse(
							responseCode = "404",
							description = "Email doesn't exist"
							)
			})
	public Response exists(
			@PathParam("email") @Email @NotNull String email
			) {
		boolean exists;

		try {
			exists = customerService.emailExists(email);
		} catch (YPCException e) {
			logger.error(e);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}

		return Response.status(exists ? Status.OK : Status.NOT_FOUND).build();			
	}

	@GET
	@Path("/{customerEmail}")
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
							responseCode = "400",
							description = "Email parameter is malformed"
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
			@PathParam("email") @Email @NotNull String email
			) {
		return ResponseWrapper.wrap(() -> customerService.findByEmail(email), Status.NOT_FOUND);	
	}

}
