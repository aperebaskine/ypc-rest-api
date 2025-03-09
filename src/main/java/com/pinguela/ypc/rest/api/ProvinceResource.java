package com.pinguela.ypc.rest.api;

import com.pinguela.yourpc.model.Province;
import com.pinguela.yourpc.service.ProvinceService;
import com.pinguela.yourpc.service.impl.ProvinceServiceImpl;
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

@Path("/provinces")
public class ProvinceResource {

	private ProvinceService provinceService;

	public ProvinceResource() {
		this.provinceService = new ProvinceServiceImpl();
	}

	@GET
	@Path("/{countryId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			method = "GET",
			operationId = "findProvincesByCountry",
			description = "Retrieve a list of provinces for the specified country.",
					responses = {
							@ApiResponse(
									responseCode = "200", 
									description = "Successfully retrieved province data",
									content = @Content(
											mediaType = "application/json",
											array = @ArraySchema(
													schema = @Schema(implementation = Province.class)
													)
											)
									),
							@ApiResponse(
									responseCode = "500",
									description = "Unknown error"
									)
			})
	public Response findByCountry(
			@PathParam("countryId") String countryId
			) {
		return ResponseWrapper.wrap(
				() -> this.provinceService.findByCountry(countryId), 
				Status.INTERNAL_SERVER_ERROR, 
				Status.INTERNAL_SERVER_ERROR
				);
	}
}
