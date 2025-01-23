package com.pinguela.ypc.rest.api;

import java.util.Locale;

import javax.validation.constraints.Min;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import com.pinguela.YPCException;
import com.pinguela.yourpc.model.dto.LocalizedProductDTO;
import com.pinguela.yourpc.service.ProductService;
import com.pinguela.yourpc.service.impl.ProductServiceImpl;

@Path("/product")
public class ProductResource {
	
	private ProductService productService;
	
	public ProductResource() {
		this.productService = new ProductServiceImpl();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response findById(
			@QueryParam("id") @Min(1) Long id,
			@CookieParam("locale") Locale locale) 
					throws YPCException {
		LocalizedProductDTO p = productService.findByIdLocalized(id, locale);
		
		ResponseBuilder rb = p == null ?
				Response.status(Status.BAD_REQUEST) :
					Response.ok(p);
		
		return rb.build();
	}
	
	public Response findBy(MultivaluedMap<String, String> params) {
		return null;
	}


}
