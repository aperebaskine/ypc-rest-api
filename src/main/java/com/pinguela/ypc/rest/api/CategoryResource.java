package com.pinguela.ypc.rest.api;

import com.pinguela.yourpc.model.dto.CategoryDTO;
import com.pinguela.yourpc.service.CategoryService;
import com.pinguela.yourpc.service.impl.CategoryServiceImpl;
import com.pinguela.ypc.rest.api.util.LocaleUtils;
import com.pinguela.ypc.rest.api.util.ResponseWrapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/categories")
public class CategoryResource {

	private CategoryService categoryService;

	public CategoryResource() {
		this.categoryService = new CategoryServiceImpl();
	}

	@GET
	@Path("{locale}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			method = "GET",
			operationId = "findAllCategories",
			description = "Return a list of all existing categories.",
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully retrieved category data",
							content = @Content(
									mediaType = "application/json",
									array = @ArraySchema(
											schema = @Schema(
													implementation = CategoryDTO.class
													)
											)
									)
							), 
					@ApiResponse(
							responseCode = "400",
							description = "Error in received parameters"
							)
			}
			)
	public Response findAll(@PathParam("locale") String locale) {
		return ResponseWrapper.wrap(
				() -> categoryService.findAll(LocaleUtils.getLocale(locale)).values(), 
				Status.BAD_REQUEST
				);
	}

}
