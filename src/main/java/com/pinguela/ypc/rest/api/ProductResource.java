package com.pinguela.ypc.rest.api;

import java.util.Locale;

import javax.validation.constraints.Min;
import javax.websocket.server.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.pinguela.yourpc.model.dto.ProductDTO;
import com.pinguela.yourpc.service.ProductService;
import com.pinguela.yourpc.service.impl.ProductServiceImpl;
import com.pinguela.ypc.rest.api.util.ResponseUtils;

@Path("products")
public class ProductResource {

	private ProductService productService;

	public ProductResource() {
		this.productService = new ProductServiceImpl();
	}

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findById(@PathParam("locale") Locale locale, 
			@PathParam("id") @Min(1) Long id) {
		return ResponseUtils.wrap(() -> productService.findById(id, locale));
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(ProductDTO dto) {
		return ResponseUtils.wrap(() -> {
			Long id = productService.create(dto);
			return productService.findById(id, null);
		});
	}
}
