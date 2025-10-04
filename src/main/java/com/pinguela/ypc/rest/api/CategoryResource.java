package com.pinguela.ypc.rest.api;

import com.pinguela.yourpc.model.dto.CategoryDTO;
import com.pinguela.yourpc.service.CategoryService;
import com.pinguela.yourpc.service.impl.CategoryServiceImpl;
import com.pinguela.ypc.rest.api.annotations.Public;
import com.pinguela.ypc.rest.api.util.LocaleUtils;
import com.pinguela.ypc.rest.api.util.ResponseWrapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Public
@Path("/")
@Tag(name = "product")
@ApiResponses(
		@ApiResponse(
				responseCode = "400",
				description = "One or more of the request parameters is malformed"
				)
		)
public class CategoryResource {

	private CategoryService categoryService;

	public CategoryResource() {
		this.categoryService = new CategoryServiceImpl();
	}

	@GET
	@Path("/categories/{locale}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			method = "GET",
			operationId = "findAllCategories",
			description = "Retrieve a list of all existing categories",
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully retrieved categories",
							content = @Content(
									mediaType = MediaType.APPLICATION_JSON,
									array = @ArraySchema(
											schema = @Schema(
													implementation = CategoryDTO.class
													)
											)
									)
							), 

			}
			)
	public Response findAll(@PathParam("locale") String locale) {
		return ResponseWrapper.wrap(
				() -> categoryService.findAll(LocaleUtils.getLocale(locale)).values(), 
				Status.BAD_REQUEST
				);
	}

}
