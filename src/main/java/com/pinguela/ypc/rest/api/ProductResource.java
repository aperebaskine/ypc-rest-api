package com.pinguela.ypc.rest.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pinguela.YPCException;
import com.pinguela.yourpc.model.ProductCriteria;
import com.pinguela.yourpc.model.Results;
import com.pinguela.yourpc.model.constants.AttributeDataTypes;
import com.pinguela.yourpc.model.constants.AttributeValueHandlingModes;
import com.pinguela.yourpc.model.dto.AttributeDTO;
import com.pinguela.yourpc.model.dto.ProductDTO;
import com.pinguela.yourpc.service.ProductService;
import com.pinguela.yourpc.service.impl.ProductServiceImpl;
import com.pinguela.ypc.rest.api.mixin.LightAttributeDTOMixin;
import com.pinguela.ypc.rest.api.mixin.ProductDTOMixin;
import com.pinguela.ypc.rest.api.processing.AttributeRangeValidator;
import com.pinguela.ypc.rest.api.util.ResponseWrapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
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

	private static Logger logger = LogManager.getLogger(ProductResource.class);

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
	public Response findById(@PathParam("locale") String locale, 
			@PathParam("id") @Min(1) Long id) {
		return ResponseWrapper.wrap(() -> productService.findByIdLocalized(id, Locale.forLanguageTag(locale)));
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
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
	@Consumes(MediaType.APPLICATION_JSON)
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
			@QueryParam("attributes")
			@ArraySchema(
					schema = @Schema(implementation = LightAttributeDTOMixin.class)
					)
			@Parameter(
					description = "List of attribute criteria, represented by an ID and list of values."
							+ " Values should contain only their ID when searching within a set of values, or their value when searching within a range of values.",
							example = "['id:2, values:[value:2600, value:5500]', 'id:45, values:[id:123, id:456]']"
					)
			List<String> attributes,
			@Context UriInfo uriInfo
			) {

		MultivaluedMap<String, String> params = uriInfo.getQueryParameters();
		ProductCriteria criteria = new ProductCriteria(name, launchDateMin, launchDateMax, 
				stockMin, stockMax, priceMin, priceMax, categoryId, buildAttributeCriteria(params, categoryId));
		return ResponseWrapper.wrap(() -> productService.findBy(criteria, Locale.forLanguageTag(locale), pos, pageSize));
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
