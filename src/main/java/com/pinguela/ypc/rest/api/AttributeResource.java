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
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Path("/")
@Tag(name = "product")
@ApiResponses(
		@ApiResponse(
				responseCode = "400",
				description = "One or more request parameters is malformed"
				)
		)
public class AttributeResource {

	private AttributeService attributeService;

	public AttributeResource() {
		this.attributeService = new AttributeServiceImpl();
	}

	@GET
	@Path("/attributes/{locale:[a-z]{2}-[A-Z]{2}}/{attributeId:[0-9]+}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			method = "GET",
			operationId = "findAttributeById",
			description = "Find data for an attribute by its ID.\n"
					+ "Optionally return values that haven't been assigned to products,"
					+ "or filter values that have been assigned to a specific category and its sub-categories",
					responses = {
							@ApiResponse(
									responseCode = "200", 
									description = "Successfully retrieved attribute data",
									content = @Content(
											mediaType = MediaType.APPLICATION_JSON,
											schema = @Schema(implementation = AttributeDTOMixin.class)
											)
									),
							@ApiResponse(
									responseCode = "404",
									description = "No attribute matching the parameters was found"
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
	@Path("/attributes/{locale:[a-z]{2}-[A-Z]{2}}/{attributeName:.+}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			method = "GET",
			operationId = "findAttributeByName",
			description = "Find data for an attribute by its name.\n"
					+ "Optionally return values that haven't been assigned to products,"
					+ "or filter values that have been assigned to a specific category and its sub-categories",
					responses = {
							@ApiResponse(
									responseCode = "200", 
									description = "Successfully retrieved data",
									content = @Content(
											mediaType = MediaType.APPLICATION_JSON,
											schema = @Schema(implementation = AttributeDTOMixin.class)
											)
									),
							@ApiResponse(
									responseCode = "404",
									description = "No attribute matching the parameters was found"
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
	@Path("/categories/{locale:[a-z]{2}-[A-Z]{2}}/{categoryId:[0-9]+}/attributes")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			method = "GET",
			operationId = "findAttributeByCategory",
			description = "Find all attributes associated with a given category.\n"
					+ "Optionally return values that haven't been assigned to products",
					responses = {
							@ApiResponse(
									responseCode = "200", 
									description = "Successfully retrieved data",
									content = @Content(
											mediaType = MediaType.APPLICATION_JSON,
											array = @ArraySchema(
													schema = @Schema(implementation = AttributeDTOMixin.class)
													)
											)
									)
			})
	public Response findByCategory(
			@PathParam("locale") String locale,
			@PathParam("categoryId") Short categoryId,
			@QueryParam("unassignedValues") @DefaultValue("false") Boolean unassignedValues
			) {
		Locale l = Locale.forLanguageTag(locale);
		return ResponseWrapper.wrap(() -> 
		attributeService.findByCategory(categoryId, l, unassignedValues).values()
				);
	}

}
