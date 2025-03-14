package com.pinguela.ypc.rest.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pinguela.ServiceException;
import com.pinguela.yourpc.service.ImageFileService;
import com.pinguela.yourpc.service.impl.ImageFileServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/image")
public class ImageResource {

	private static Logger logger = LogManager.getLogger(ImageResource.class);

	private ImageFileService imageFileService;

	public ImageResource() {
		this.imageFileService = new ImageFileServiceImpl();
	}

	@GET
	@Path("/product/{id}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Operation(
			method = "GET",
			operationId = "findProductImageById",
			description = "Return product image for specified ID", 
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully retrieved product image",
							content = @Content(
									mediaType = MediaType.APPLICATION_OCTET_STREAM
									)
							), 
					@ApiResponse(
							responseCode = "404",
							description = "Image not found"
							)
			})
	public Response getProductImage(
			@PathParam("id") Integer productId
			) {
		try {
			List<InputStream> images = this.imageFileService.getInputStreams("product", productId);

			if (images.size() < 1) {
				return Response.status(Status.NOT_FOUND).build();
			}

			for (int i = 1; i < images.size(); i++) {
				images.get(i).close();
			}

			return Response.ok(images.get(0)).build();

		} catch (ServiceException | IOException e) {
			logger.error(e);
			throw new WebApplicationException(Status.BAD_REQUEST);
		}
	}

}
