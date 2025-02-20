package com.pinguela.ypc.rest.api;

import java.util.Locale;

import com.pinguela.yourpc.model.dto.AttributeDTO;
import com.pinguela.yourpc.service.AttributeService;
import com.pinguela.yourpc.service.impl.AttributeServiceImpl;
import com.pinguela.ypc.rest.api.mixin.AttributeDTOMixin;
import com.pinguela.ypc.rest.api.util.ResponseWrapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

@Path("/attribute")
public class AttributeResource {

	private AttributeService attributeService;

	public AttributeResource() {
		this.attributeService = new AttributeServiceImpl();
	}

	@GET
	@Path("/{locale}/{id: ^\\d$}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			method = "GET",
			operationId = "findAttributeById",
			description = "Find data for an attribute by its ID.\n"
					+ "Optionally return values that haven't been assigned to products.",
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
			@PathParam("id") @Min(1) Integer id,
			@QueryParam("categoryId") Short categoryId,
			@QueryParam("unassignedValues") @DefaultValue("false") Boolean unassignedValues
			) {
		return ResponseWrapper.wrap(
				() -> attributeService.findById(id, Locale.forLanguageTag(locale), unassignedValues, categoryId), 
				Status.NOT_FOUND
				);
	}

	@GET
	@Path("/{locale}")
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
	public Response findByName(
			@PathParam("locale") String locale,
			@QueryParam("name") String name,
			@QueryParam("categoryId") Short categoryId,
			@QueryParam("unassignedValues") @DefaultValue("false") Boolean unassignedValues
			) {
		Locale l = Locale.forLanguageTag(locale);
		return ResponseWrapper.wrap(() -> name == null ?
				attributeService.findByCategory(categoryId, l, unassignedValues) :
					new AttributeDTO<?>[] {attributeService.findByName(name, l, unassignedValues, categoryId)}
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
									),
							@ApiResponse(
									responseCode = "404",
									description = "No attribute found with the specified name and locale."
									)
			})
	public Response findByCategory(
			@PathParam("locale") String locale,
			@QueryParam("categoryId") Short categoryId,
			@QueryParam("unassignedValues") @DefaultValue("false") Boolean unassignedValues
			) {
		Locale l = Locale.forLanguageTag(locale);
		return ResponseWrapper.wrap(() -> 
				attributeService.findByCategory(categoryId, l, unassignedValues)
				);
	}

}
