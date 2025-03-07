package com.pinguela.ypc.rest.api;

import com.pinguela.InvalidLoginCredentialsException;
import com.pinguela.YPCException;
import com.pinguela.yourpc.model.Customer;
import com.pinguela.yourpc.service.CustomerService;
import com.pinguela.yourpc.service.impl.CustomerServiceImpl;
import com.pinguela.ypc.rest.api.json.param.ParameterProcessor;
import com.pinguela.ypc.rest.api.model.ErrorLog;
import com.pinguela.ypc.rest.api.util.ResponseWrapper;
import com.pinguela.ypc.rest.api.validation.Validators;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/user")
public class UserResource {

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

		String sessionToken;

		try {
			sessionToken = customerService.login(email, password);

		} catch (InvalidLoginCredentialsException e) {
			return Response.status(Status.NOT_FOUND).build();
		} catch (YPCException e) {
			return Response.status(Status.BAD_REQUEST).build();
		}

		return ResponseWrapper.wrap(() -> sessionToken);
	}

	@POST
	@Path("/register")
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
									schema = @Schema(implementation = Customer.class)
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
			@FormParam("documentTypeId") @NotNull String documentTypeId,
			@FormParam("documentNumber") @NotNull String documentNumber,
			@FormParam("phoneNumber") @NotNull String phoneNumber,
			@FormParam("email") @NotNull String email,
			@FormParam("password") @NotNull String password
			) {
		return new ParameterProcessor()
				.validate("email", email, Validators.isUnusedEmail())
				.buildResponse(() -> {
					Customer c = new Customer();
					c.setFirstName(firstName);
					c.setLastName1(lastName1);
					c.setLastName2(lastName2);
					c.setDocumentTypeId(documentTypeId);
					c.setDocumentNumber(documentNumber);
					c.setPhoneNumber(phoneNumber);
					c.setEmail(email);
					c.setUnencryptedPassword(password);
					
					Integer id = customerService.register(c);
					c.setId(id);
					
					return c;
				});
	}

}
