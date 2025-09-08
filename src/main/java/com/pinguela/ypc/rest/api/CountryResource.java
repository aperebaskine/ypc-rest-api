package com.pinguela.ypc.rest.api;

import com.pinguela.yourpc.model.Country;
import com.pinguela.yourpc.service.CountryService;
import com.pinguela.yourpc.service.impl.CountryServiceImpl;
import com.pinguela.ypc.rest.api.annotations.Public;
import com.pinguela.ypc.rest.api.util.ResponseWrapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Public
@Path("/countries")
public class CountryResource {

	private CountryService countryService;

	public CountryResource() {
		this.countryService = new CountryServiceImpl();
	}

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			method = "GET",
			operationId = "findAllCountries",
			description = "Retrieve a list of all countries.",
					responses = {
							@ApiResponse(
									responseCode = "200", 
									description = "Successfully retrieved country data",
									content = @Content(
											mediaType = "application/json",
											array = @ArraySchema(
													schema = @Schema(implementation = Country.class)
													)
											)
									),
							@ApiResponse(
									responseCode = "500",
									description = "Unknown error"
									)
			})
	public Response findAll() {
		return ResponseWrapper.wrap(
				() -> this.countryService.findAll(), 
				Status.INTERNAL_SERVER_ERROR, 
				Status.INTERNAL_SERVER_ERROR
				);
	}

}
