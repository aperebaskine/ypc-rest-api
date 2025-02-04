package com.pinguela.ypc.rest.api;

import java.util.Locale;

import javax.validation.constraints.Min;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import com.pinguela.yourpc.model.ProductCriteria;
import com.pinguela.yourpc.model.Results;
import com.pinguela.yourpc.model.dto.LocalizedProductDTO;
import com.pinguela.yourpc.service.ProductService;
import com.pinguela.yourpc.service.impl.ProductServiceImpl;
import com.pinguela.ypc.rest.api.constants.Parameters;
import com.pinguela.ypc.rest.api.util.ResponseUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/product")
public class ProductResource {

	private ProductService productService;

	public ProductResource() {
		this.productService = new ProductServiceImpl();
	}

	@GET
	@Path("/{locale}/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			method = "GET",
			description = "Return product data in the specified language", 
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully retrieved product data",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(implementation = LocalizedProductDTO.class)
									)
							), 
					@ApiResponse(
							responseCode = "404",
							description = "Product not found"
							)
			})
	public Response findByIdLocalized(@PathParam("locale") Locale locale, 
			@PathParam("id") @Min(1) Long id) {
		return ResponseUtils.wrap(() -> productService.findByIdLocalized(id, locale));
	}

	@POST // Justificado por el tamaño de la URL por las búsqueda complejas por Atribute
	@Path("/{locale}/search")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			method = "GET",
			description = "Return a list of products", 
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully retrieved product data",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(implementation = ProductResults.class)
									)
							), 
					@ApiResponse(
							responseCode = "404",
							description = "Products not found"
							)
			})
	public Response findBy(
			@PathParam("locale") Locale locale, 
			MultivaluedMap<String, String> parameters	 
			) {
		
		ProductCriteria criteria = new ProductCriteria();
		return ResponseUtils.wrap(() -> productService.findBy(criteria, locale, 0, 0));
	}

	private static class ProductResults extends Results<LocalizedProductDTO> {}

}
