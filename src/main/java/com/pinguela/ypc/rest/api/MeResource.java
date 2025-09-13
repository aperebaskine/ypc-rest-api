package com.pinguela.ypc.rest.api;

import java.util.Locale;

import com.pinguela.yourpc.model.CustomerOrder;
import com.pinguela.yourpc.service.AddressService;
import com.pinguela.yourpc.service.CustomerOrderService;
import com.pinguela.yourpc.service.CustomerService;
import com.pinguela.yourpc.service.impl.AddressServiceImpl;
import com.pinguela.yourpc.service.impl.CustomerOrderServiceImpl;
import com.pinguela.yourpc.service.impl.CustomerServiceImpl;
import com.pinguela.ypc.rest.api.constants.Roles;
import com.pinguela.ypc.rest.api.model.AddressDTOMixin;
import com.pinguela.ypc.rest.api.model.CustomerDTOMixin;
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
import jakarta.ws.rs.PATCH;
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
			operationId = "findMe",
			description = "Retrieve the currently authenticated customer's data", 
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
			operationId = "findMyAddresses",
			description = "Retrieve all address for the authenticated customer.",
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully retrieved addresses",
							content = @Content(
									mediaType = MediaType.APPLICATION_JSON,
									array = @ArraySchema(
											schema = @Schema(implementation = AddressDTOMixin.class)
											)
									)
							),
					@ApiResponse(
							responseCode = "401",
							description = "Caller is unauthenticated"
							)
			})
	public Response findAddresses() {
		return ResponseWrapper.wrap(() -> this.addressService.findByCustomer(getUserId()), Status.OK);
	}

	@GET
	@Path("/{locale}/orders")
	@Operation(
			method = "GET",
			operationId = "findMyOrders",
			description = "Retrieve the authenticated customer's orders",
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully retrieved orders",
							content = @Content(
									mediaType = "application/json",
									array = @ArraySchema(
											schema = @Schema(implementation = CustomerOrder.class)
											)
									)
							),
					@ApiResponse(
							responseCode = "400",
							description = "Malformed parameter(s) in request"
							),
					@ApiResponse(
							responseCode = "401",
							description = "Caller is unauthenticated"
							)
			})
	public Response findOrders(
			@PathParam("locale") String locale
			) {
		Locale l = LocaleUtils.getLocale(locale);
		return ResponseWrapper.wrap(() -> orderService.findByCustomer(getUserId(), l));
	}

}
