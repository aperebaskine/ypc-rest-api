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
import com.pinguela.yourpc.service.AddressService;
import com.pinguela.yourpc.service.CustomerOrderService;
import com.pinguela.yourpc.service.CustomerService;
import com.pinguela.yourpc.service.impl.CustomerOrderServiceImpl;
import com.pinguela.ypc.rest.api.util.AuthUtils;
import com.pinguela.ypc.rest.api.util.LocaleUtils;
import com.pinguela.ypc.rest.api.util.ResponseWrapper;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.Email;
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

@Path("/order")
@SecurityRequirement(name = "bearerAuth")
public class OrderResource {

	private CustomerService customerService;
	private AddressService addressService;
	private CustomerOrderService orderService;

	public OrderResource() {
		this.orderService = new CustomerOrderServiceImpl();
	}

	@GET
	@Path("/{locale}/{id}")
	public Response findById(
			@PathParam("locale") String locale,
			@PathParam("id") Long id) {
		return ResponseWrapper.wrap(() -> orderService.findById(id, LocaleUtils.getLocale(locale)));
	}

	@GET
	@Path("/{locale}")
	public Response findAll(
			@PathParam("locale") String locale,
			@Context ContainerRequestContext context
			) {

		Locale l = LocaleUtils.getLocale(locale);

		String token = AuthUtils.getSessionToken(context);
		Customer c;

		try {
			c = this.customerService.findBySessionToken(token);
		} catch (ServiceException | DataException e) {
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}

		Integer customerId = c.getId();

		return ResponseWrapper.wrap(() -> orderService.findByCustomer(customerId, l));
	}

	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(
			@FormParam("billingAddressId") Integer billingAddressId,
			@FormParam("shippingAddressId") Integer shippingAddressId,
			@FormParam("orderLines") List<OrderLine> orderLines,
			@Context ContainerRequestContext context
			) {

		String token = AuthUtils.getSessionToken(context);

		try {
			Customer c = this.customerService.findBySessionToken(token);

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
