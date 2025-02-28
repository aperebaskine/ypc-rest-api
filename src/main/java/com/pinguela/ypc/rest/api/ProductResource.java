package com.pinguela.ypc.rest.api;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pinguela.YPCException;
import com.pinguela.yourpc.model.ProductCriteria;
import com.pinguela.yourpc.model.Results;
import com.pinguela.yourpc.model.dto.AttributeDTO;
import com.pinguela.yourpc.model.dto.ProductDTO;
import com.pinguela.yourpc.service.ProductService;
import com.pinguela.yourpc.service.impl.ProductServiceImpl;
import com.pinguela.ypc.rest.api.mixin.ProductDTOMixin;
import com.pinguela.ypc.rest.api.util.ResponseWrapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/product")
public class ProductResource {

	private static Logger logger = LogManager.getLogger(ProductResource.class);

	private ProductService productService;

	public ProductResource() {
		this.productService = new ProductServiceImpl();
	}

	@GET
	@Path("/{locale}/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			method = "GET",
			operationId = "findProductById",
			description = "Return product data in the specified language", 
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully retrieved product data",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(implementation = ProductDTOMixin.class)
									)
							), 
					@ApiResponse(
							responseCode = "404",
							description = "Product not found"
							)
			})
	public Response findById(@PathParam("locale") String locale, 
			@PathParam("id") @Min(1) Long id) {
		return ResponseWrapper.wrap(() -> productService.findByIdLocalized(id, Locale.forLanguageTag(locale)));
	}

	public Response create(@BeanParam ProductDTO dto) {

		Long id = null;

		try {
			id = productService.create(dto);
		} catch (YPCException e) {
			logger.error("Error while inserting Product into database: {}", dto, e);
		}

		if (id == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}

		dto.setId(id);
		return Response.ok(dto).build();
	}

	@GET
	@Path("/{locale}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			method = "GET",
			operationId = "findProductBy",
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
			@PathParam("locale") String locale, 
			@QueryParam("name") String name, 
			@QueryParam("launchDateFrom") 
			@Parameter(
					schema = @Schema(
							type = "string", 
							format = "date"
							)
					)
			Date launchDateMin,
			@QueryParam("launchDateTo") 
			@Parameter(
					schema = @Schema(
							type = "string", 
							format = "date"
							)
					)
			Date launchDateMax,
			@QueryParam("stockMin") Integer stockMin,
			@QueryParam("stockMax") Integer stockMax,
			@QueryParam("priceMin") Double priceMin,
			@QueryParam("priceMax") Double priceMax,
			@QueryParam("categoryId") Short categoryId,
			@QueryParam("pos") @NotNull Integer pos,
			@QueryParam("pageSize") @NotNull Integer pageSize,
			@QueryParam("attributes")
			@Parameter(
					schema = @Schema(
							type = "string",
							format = "byte",
							description = "Attribute list for GET requests in JSON format and Base64 encoded,"
									+ " represented by its ID and values.",
							example = "[{ id: 2, values: [2500, 3500] }, { id: 25, values: [true] }]"
							),
					description = "List of attribute criteria, represented by their ID and list of values to filter."
					)
			List<AttributeDTO<?>> attributes
			) {

		ProductCriteria criteria = new ProductCriteria(name, launchDateMin, launchDateMax, 
				stockMin, stockMax, priceMin, priceMax, categoryId, attributes);
		return ResponseWrapper.wrap(() -> productService.findBy(criteria, Locale.forLanguageTag(locale), pos, pageSize));
	}

	private static class ProductResults extends Results<ProductDTOMixin> {}

}
