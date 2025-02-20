package com.pinguela.ypc.rest.api;

import java.util.Date;
import java.util.Locale;

import com.pinguela.yourpc.model.CustomerOrderCriteria;
import com.pinguela.yourpc.service.CustomerOrderService;
import com.pinguela.yourpc.service.impl.CustomerOrderServiceImpl;
import com.pinguela.ypc.rest.api.util.LocaleUtils;
import com.pinguela.ypc.rest.api.util.ResponseWrapper;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

@Path("/order")
public class OrderResource {

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
	public Response findBy(
			@PathParam("locale") String locale,
			@QueryParam("customerId") Integer customerId,
			@QueryParam("customerEmail") @Email String customerEmail,
			@QueryParam("minAmount") @Min(0) Double minAmount,
			@QueryParam("maxAmount") @Min(0) Double maxAmount,
			@QueryParam("minDate") Date minDate,
			@QueryParam("maxDate") Date maxDate,
			@QueryParam("state") String state) {
		
		Locale l = LocaleUtils.getLocale(locale);
		
		if (customerId != null) {
			return ResponseWrapper.wrap(() -> orderService.findByCustomer(customerId, l));
		}
		
		CustomerOrderCriteria criteria = new CustomerOrderCriteria(customerId, customerEmail, 
				minAmount, maxAmount, minDate, maxDate, state);
		
		return ResponseWrapper.wrap(() -> orderService.findBy(criteria, l));
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
			@QueryParam("state") String state) {
				
		CustomerOrderCriteria criteria = new CustomerOrderCriteria(customerId, customerEmail, 
				minAmount, maxAmount, minDate, maxDate, state);
		
		return ResponseWrapper.wrap(() -> orderService.getRanges(criteria));
	}
	
	
}
