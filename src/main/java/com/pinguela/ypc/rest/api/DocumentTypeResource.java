package com.pinguela.ypc.rest.api;

import com.pinguela.yourpc.model.DocumentType;
import com.pinguela.yourpc.service.DocumentTypeService;
import com.pinguela.yourpc.service.impl.DocumentTypeServiceImpl;
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

@Path("/doctypes")
public class DocumentTypeResource {
		
	private DocumentTypeService docTypeService;
	
	public DocumentTypeResource() {
		this.docTypeService = new DocumentTypeServiceImpl();
	}
	
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			method = "GET",
			operationId = "findAllDoctypes",
			description = "Find all supported document types", 
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully retrieved document type list.",
							content = @Content(
									mediaType = "application/json",
									array = @ArraySchema(
											schema = @Schema(implementation = DocumentType.class)
											)
									)
							),
					@ApiResponse(
							responseCode = "500",
							description = "Unknown error occured"
							)
			})
	public Response findAll() {
		return ResponseWrapper.wrap(() -> this.docTypeService.findAll().values(), Status.INTERNAL_SERVER_ERROR, Status.INTERNAL_SERVER_ERROR);
	}

}
