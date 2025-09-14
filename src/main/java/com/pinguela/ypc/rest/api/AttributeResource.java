package com.pinguela.ypc.rest.api;

import java.util.Locale;

import com.pinguela.yourpc.service.AttributeService;
import com.pinguela.yourpc.service.impl.AttributeServiceImpl;
import com.pinguela.ypc.rest.api.annotations.Public;
import com.pinguela.ypc.rest.api.model.AttributeDTOMixin;
import com.pinguela.ypc.rest.api.util.LocaleUtils;
import com.pinguela.ypc.rest.api.util.ResponseWrapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Public
@Path("/attributes")
@Tag(name = "product")
public class AttributeResource {

	private AttributeService attributeService;

	public AttributeResource() {
		this.attributeService = new AttributeServiceImpl();
	}

	@GET
	@Path("/{locale}/{attributeId:\\d+}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			method = "GET",
			operationId = "findAttributeById",
			description = "Find data for an attribute by its ID.\n"
					+ "Optionally return values that haven't been assigned to products,"
					+ "or filter values that have been assigned to a specific category of products.",
					responses = {
							@ApiResponse(
									responseCode = "200", 
									description = "Successfully retrieved attribute data",
									content = @Content(
											mediaType = "application/json",
											schema = @Schema(implementation = AttributeDTOMixin.class)
											)
									), 
							@ApiResponse(
									responseCode = "400",
									description = "Error in received parameters"
									),
							@ApiResponse(
									responseCode = "404",
									description = "No attribute found with the specified ID."
									)
			})
	public Response findById(
			@PathParam("locale") String locale,
			@PathParam("attributeId") @Min(1) Integer attributeId,
			@QueryParam("categoryId") Short categoryId,
			@QueryParam("unassignedValues") @DefaultValue("false") Boolean unassignedValues
			) {
		return ResponseWrapper.wrap(
				() -> attributeService.findById(attributeId, LocaleUtils.getLocale(locale), unassignedValues, categoryId), 
				Status.NOT_FOUND
				);
	}

	@GET
	@Path("/{locale}/{attributeName}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			method = "GET",
			operationId = "findAttributeByName",
			description = "Find data for an attribute by its name or category.\n"
					+ "Optionally return values that haven't been assigned to products, "
					+ "*or* filter values assigned to products of a specific category.",
					responses = {
							@ApiResponse(
									responseCode = "200", 
									description = "Successfully retrieved attribute data",
									content = @Content(
											mediaType = "application/json",
											schema = @Schema(implementation = AttributeDTOMixin.class)
											)
									), 
							@ApiResponse(
									responseCode = "400",
									description = "Error in received parameters"
									),
							@ApiResponse(
									responseCode = "404",
									description = "No attribute found with the specified name and locale."
									)
			})
	public Response findByName(
			@PathParam("locale") String locale,
			@PathParam("attributeName") String attributeName,
			@QueryParam("categoryId") Short categoryId,
			@QueryParam("unassignedValues") @DefaultValue("false") Boolean unassignedValues
			) {
		Locale l = Locale.forLanguageTag(locale);
		return ResponseWrapper.wrap(
				() -> attributeService.findByName(attributeName, l, unassignedValues, categoryId), 
				Status.NOT_FOUND
				);
	}

	@GET
	@Path("/{locale}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			method = "GET",
			operationId = "findAttributeByCategory",
			description = "Find data for an attribute by its name or category.\n"
					+ "Optionally return values that haven't been assigned to products, "
					+ "*or* filter values assigned to products of a specific category.",
					responses = {
							@ApiResponse(
									responseCode = "200", 
									description = "Successfully retrieved attribute data",
									content = @Content(
											mediaType = "application/json",
											array = @ArraySchema(
													schema = @Schema(implementation = AttributeDTOMixin.class)
													)
											)
									), 
							@ApiResponse(
									responseCode = "400",
									description = "Error in received parameters"
									)
			})
	public Response findByCategory(
			@PathParam("locale") String locale,
			@QueryParam("categoryId") Short categoryId,
			@QueryParam("unassignedValues") @DefaultValue("false") Boolean unassignedValues
			) {
		Locale l = Locale.forLanguageTag(locale);
		return ResponseWrapper.wrap(() -> 
		attributeService.findByCategory(categoryId, l, unassignedValues).values()
				);
	}

}
