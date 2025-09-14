package com.pinguela.ypc.rest.api;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.pinguela.DataException;
import com.pinguela.ServiceException;
import com.pinguela.yourpc.model.Address;
import com.pinguela.yourpc.model.Customer;
import com.pinguela.yourpc.model.CustomerOrder;
import com.pinguela.yourpc.model.CustomerOrderCriteria;
import com.pinguela.yourpc.model.OrderLine;
import com.pinguela.yourpc.model.dto.ProductDTO;
import com.pinguela.yourpc.service.AddressService;
import com.pinguela.yourpc.service.CustomerOrderService;
import com.pinguela.yourpc.service.CustomerService;
import com.pinguela.yourpc.service.ProductService;
import com.pinguela.yourpc.service.impl.AddressServiceImpl;
import com.pinguela.yourpc.service.impl.CustomerOrderServiceImpl;
import com.pinguela.yourpc.service.impl.CustomerServiceImpl;
import com.pinguela.yourpc.service.impl.ProductServiceImpl;
import com.pinguela.ypc.rest.api.constants.Roles;
import com.pinguela.ypc.rest.api.util.LocaleUtils;
import com.pinguela.ypc.rest.api.util.ResponseWrapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/")
@RolesAllowed({Roles.ADMIN, Roles.SUPPORT})
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "order")
@ApiResponses({
	@ApiResponse(
			responseCode = "400",
			description = "One or more request parameters is malformed"
			),
	@ApiResponse(
			responseCode = "401",
			description = "Caller is unauthenticated"
			),
	@ApiResponse(
			responseCode = "403",
			description = "Caller lacks sufficient permissions to access the endpoint"
			)
})
public class OrderResource {

	private ProductService productService;
	private CustomerService customerService;
	private AddressService addressService;
	private CustomerOrderService orderService;

	public OrderResource() {
		this.productService = new ProductServiceImpl();
		this.customerService = new CustomerServiceImpl();
		this.addressService = new AddressServiceImpl();
		this.orderService = new CustomerOrderServiceImpl();
	}

	@GET
	@Path("/orders/{locale}/{orderId}")
	@Operation(
			method = "GET",
			operationId = "findOrderById",
			description = "Retrieve an order by its ID",
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully retrieved order data",
							content = @Content(
									mediaType = MediaType.APPLICATION_JSON,
									schema = @Schema(implementation = CustomerOrder.class)
									)
							)
			})
	public Response findById(
			@PathParam("locale") String locale,
			@PathParam("orderId") Long orderId
			) {
		return ResponseWrapper.wrap(() -> orderService.findById(orderId, LocaleUtils.getLocale(locale)));
	}

	@GET
	@Path("/customers/{customerId}/orders/{locale}")
	@Operation(
			method = "GET",
			operationId = "findOrdersByCustomer",
			description = "Retrieve all orders for a given customer",
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully retrieved user's orders",
							content = @Content(
									mediaType = MediaType.APPLICATION_JSON,
									array = @ArraySchema(
											schema = @Schema(implementation = CustomerOrder.class)
											)
									)
							)
			})
	public Response findByCustomer(
			@PathParam("customerId") Integer customerId,
			@PathParam("locale") String locale,
			@Context ContainerRequestContext context
			) {
		Locale l = LocaleUtils.getLocale(locale);
		return ResponseWrapper.wrap(() -> orderService.findByCustomer(customerId, l));
	}

	@POST
	@Path("/orders")
	@Consumes({MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			method = "Post",
			operationId = "createOrder",
			description = "Place an order for a given customer",
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully placed order",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(implementation = CustomerOrder.class)
									)
							)
			})
	public Response create(
			@FormParam("customerId") @NotNull Integer customerId,
			@FormParam("billingAddressId") @NotNull Integer billingAddressId,
			@FormParam("shippingAddressId") @NotNull Integer shippingAddressId,
			@FormParam("orderLines")
			@Parameter(
					schema = @Schema(
							type = "string"
							)
					)
			List<OrderLine> orderLines,
			@Context ContainerRequestContext context
			) {

		for (OrderLine ol: orderLines) {
			try {
				ProductDTO p = this.productService.findById(ol.getProductId().longValue(),
						LocaleUtils.getDefault());
				ol.setPurchasePrice(p.getPurchasePrice());
			} catch (Exception e) {
				throw new WebApplicationException(Status.BAD_REQUEST);

			}
		}

		try {
			Customer c = this.customerService.findById(customerId);

			Address billing = this.addressService.findById(billingAddressId);
			Address shipping = this.addressService.findById(shippingAddressId);

			if (!billing.getCustomerId().equals(c.getId())
					|| !shipping.getCustomerId().equals(c.getId())) {
				throw new WebApplicationException(Status.BAD_REQUEST);
			}

			CustomerOrder co = new CustomerOrder();
			co.setCustomerId(c.getId());
			co.setBillingAddressId(billingAddressId);
			co.setShippingAddressId(shippingAddressId);
			co.setOrderLines(orderLines);

			co.setState("PND");

			Double total = co.getOrderLines().stream()
					.map((orderLine) -> orderLine.getQuantity() * orderLine.getSalePrice())
					.reduce((t, u) -> t + u)
					.get();

			co.setTotalPrice(total);

			Long id = this.orderService.create(co);

			if (id == null) {
				throw new WebApplicationException(Status.BAD_REQUEST);
			}

			co.setId(id);
			return Response.ok(co).build();

		} catch (ServiceException | DataException e) {
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}
	}

	@GET
	@Path("/ranges")
	public Response getRanges(
			@QueryParam("customerId") Integer customerId,
			@QueryParam("customerEmail") @Email String customerEmail,
			@QueryParam("minAmount") Double minAmount,
			@QueryParam("maxAmount") Double maxAmount,
			@QueryParam("minDate") Date minDate,
			@QueryParam("maxDate") Date maxDate,
			@QueryParam("state") String state
			) {

		CustomerOrderCriteria criteria = new CustomerOrderCriteria(customerId, customerEmail, 
				minAmount, maxAmount, minDate, maxDate, state);

		return ResponseWrapper.wrap(() -> orderService.getRanges(criteria));
	}


}
