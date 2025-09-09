package com.pinguela.ypc.rest.api;

import java.util.Locale;

import com.pinguela.yourpc.model.Address;
import com.pinguela.yourpc.model.Customer;
import com.pinguela.yourpc.service.AddressService;
import com.pinguela.yourpc.service.CustomerOrderService;
import com.pinguela.yourpc.service.CustomerService;
import com.pinguela.yourpc.service.impl.AddressServiceImpl;
import com.pinguela.yourpc.service.impl.CustomerOrderServiceImpl;
import com.pinguela.yourpc.service.impl.CustomerServiceImpl;
import com.pinguela.ypc.rest.api.constants.Roles;
import com.pinguela.ypc.rest.api.model.UserPrincipal;
import com.pinguela.ypc.rest.api.util.LocaleUtils;
import com.pinguela.ypc.rest.api.util.ResponseWrapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.SecurityContext;

@Path("/me")
@RolesAllowed(Roles.CUSTOMER)
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "me")
public class MeResource {

	private CustomerService customerService;
	private AddressService addressService;
	private CustomerOrderService orderService;

	@Context 
	private SecurityContext securityContext;

	public MeResource() {
		customerService = new CustomerServiceImpl();
		addressService = new AddressServiceImpl();
		orderService = new CustomerOrderServiceImpl();
	}

	private Integer getUserId() {
		UserPrincipal principal = (UserPrincipal) securityContext.getUserPrincipal();
		return principal.getId();
	}

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			method = "GET",
			operationId = "findAuthenticatedCustomer",
			description = "Retrieve user data from session token", 
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully retrieved customer information",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(implementation = Customer.class)
									)
							),
					@ApiResponse(
							responseCode = "401",
							description = "Caller is unauthenticated"
							)
			})
	public Response find() {
		return ResponseWrapper.wrap(() -> customerService.findById(getUserId()), Status.NOT_FOUND);
	}

	@GET
	@Path("/addresses")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			method = "GET",
			operationId = "findAuthenticatedCustomerAddresses",
			description = "Retrieve all address for the authenticated customer.",
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully retrieved addresses",
							content = @Content(
									mediaType = "application/json",
									array = @ArraySchema(
											schema = @Schema(implementation = Address.class)
											)
									)
							),
					@ApiResponse(
							responseCode = "500",
							description = "Unknown error"
							)
			})
	public Response findAddresses() {
		return ResponseWrapper.wrap(() -> this.addressService.findByCustomer(getUserId()), Status.OK);
	}

	@GET
	@Path("/{locale}/orders")
	@Operation(
			method = "GET",
			operationId = "findAuthenticatedCustomerOrders",
			description = "Retrieve all orders from the user.",
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully retrieved orders",
							content = @Content(
									mediaType = "application/json",
									array = @ArraySchema(
											schema = @Schema(implementation = Address.class)
											)
									)
							),
					@ApiResponse(
							responseCode = "400",
							description = "Error in parameter"
							)
			})
	public Response findOrders(
			@PathParam("locale") String locale
			) {
		Locale l = LocaleUtils.getLocale(locale);
		return ResponseWrapper.wrap(() -> orderService.findByCustomer(getUserId(), l));
	}

}
