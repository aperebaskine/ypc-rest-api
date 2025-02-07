package com.pinguela.ypc.rest.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import com.pinguela.YPCException;
import com.pinguela.rest.api.mixin.ProductDTOMixin;
import com.pinguela.yourpc.model.ProductCriteria;
import com.pinguela.yourpc.model.Results;
import com.pinguela.yourpc.model.constants.AttributeDataTypes;
import com.pinguela.yourpc.model.constants.AttributeValueHandlingModes;
import com.pinguela.yourpc.model.dto.AttributeDTO;
import com.pinguela.yourpc.service.ProductService;
import com.pinguela.yourpc.service.impl.ProductServiceImpl;
import com.pinguela.ypc.rest.api.processing.AttributeRangeValidator;
import com.pinguela.ypc.rest.api.util.ResponseUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.UriInfo;

@Path("/product")
public class ProductResource {

	private static final Pattern ATTRIBUTE_PARAMETER_REGEX = Pattern.compile("attr\\.[A-Z]{3}\\.[0-9]+");
	private static final AttributeRangeValidator RANGE_VALIDATOR = AttributeRangeValidator.getInstance();

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
									schema = @Schema(implementation = ProductDTOMixin.class)
									)
							), 
					@ApiResponse(
							responseCode = "404",
							description = "Product not found"
							)
			})
	public Response findByIdLocalized(@PathParam("locale") String locale, 
			@PathParam("id") @Min(1) Long id) {
		return ResponseUtils.wrap(() -> productService.findByIdLocalized(id, Locale.forLanguageTag(locale)));
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
			@PathParam("locale") String locale, 
			@QueryParam("name") String name, 
			@QueryParam("launchDateFrom") Date launchDateMin,
			@QueryParam("launchDateTo") Date launchDateMax,
			@QueryParam("stockMin") Integer stockMin,
			@QueryParam("stockMax") Integer stockMax,
			@QueryParam("priceMin") Double priceMin,
			@QueryParam("priceMax") Double priceMax,
			@QueryParam("categoryId") Short categoryId,
			@QueryParam("pos") @NotNull Integer pos,
			@QueryParam("pageSize") @NotNull Integer pageSize,
			@Context UriInfo uriInfo
			) {

		MultivaluedMap<String, String> params = uriInfo.getQueryParameters();
		ProductCriteria criteria = new ProductCriteria(name, launchDateMin, launchDateMax, 
				stockMin, stockMax, priceMin, priceMax, categoryId, buildAttributeCriteria(params, categoryId));
		return ResponseUtils.wrap(() -> productService.findBy(criteria, Locale.forLanguageTag(locale), pos, pageSize));
	}

	private static List<AttributeDTO<?>> buildAttributeCriteria(MultivaluedMap<String, String> parameterMap, Short categoryId) {

		List<AttributeDTO<?>> list = new ArrayList<AttributeDTO<?>>();

		Iterator<String> attributeKeyIterator = 
				parameterMap.keySet().stream().filter(t -> ATTRIBUTE_PARAMETER_REGEX.matcher(t).matches()).iterator();

		while (attributeKeyIterator.hasNext()) {
			String key = attributeKeyIterator.next();
			String[] keyComponents = key.split("\\.");
			String dataTypeIdentifier = keyComponents[1];

			if (!AttributeDataTypes.isValidType(dataTypeIdentifier)) {
				throw new WebApplicationException(Status.BAD_REQUEST);
			}

			AttributeDTO<?> dto = AttributeDTO.getInstance(dataTypeIdentifier);
			dto.setId(Integer.valueOf(keyComponents[2]));

			List<String> parameters = parameterMap.get(key);
			for (String parameter : parameters) {
				try {
					dto.addValue(null, parameter);
				} catch (IllegalArgumentException e) {
					throw new WebApplicationException(Status.BAD_REQUEST);
				}
			}

			try {
				if (AttributeValueHandlingModes.RANGE != dto.getValueHandlingMode()
						|| RANGE_VALIDATOR.validate(dto, categoryId)) {
					list.add(dto);
				}
			} catch (YPCException e) {
				throw new WebApplicationException(Status.BAD_REQUEST);
			}
		}

		return list;
	}

	private static class ProductResults extends Results<ProductDTOMixin> {}

}
