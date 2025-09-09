package com.pinguela.ypc.rest.api;

import com.pinguela.yourpc.model.City;
import com.pinguela.yourpc.service.CityService;
import com.pinguela.yourpc.service.impl.CityServiceImpl;
import com.pinguela.ypc.rest.api.annotations.Public;
import com.pinguela.ypc.rest.api.util.ResponseWrapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Public
@Path("/cities")
@Tag(name = "geo")
public class CityResource {
	
	private CityService cityService;

	public CityResource() {
		this.cityService = new CityServiceImpl();
	}

	@GET
	@Path("/{provinceId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			method = "GET",
			operationId = "findCitiesByProvince",
			description = "Retrieve a list of cities for the specified province.",
					responses = {
							@ApiResponse(
									responseCode = "200", 
									description = "Successfully retrieved city data",
									content = @Content(
											mediaType = "application/json",
											array = @ArraySchema(
													schema = @Schema(implementation = City.class)
													)
											)
									),
							@ApiResponse(
									responseCode = "500",
									description = "Unknown error"
									)
			})
	public Response findByProvince(
			@PathParam("provinceId") Integer provinceId
			) {
		return ResponseWrapper.wrap(
				() -> this.cityService.findByProvince(provinceId), 
				Status.INTERNAL_SERVER_ERROR, 
				Status.INTERNAL_SERVER_ERROR
				);
	}
}
