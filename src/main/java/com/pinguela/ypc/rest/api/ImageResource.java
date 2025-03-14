package com.pinguela.ypc.rest.api;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.pinguela.DataException;
import com.pinguela.ServiceException;
import com.pinguela.yourpc.model.Customer;
import com.pinguela.yourpc.model.ImageEntry;
import com.pinguela.yourpc.service.CustomerService;
import com.pinguela.yourpc.service.ImageFileService;
import com.pinguela.yourpc.service.impl.CustomerServiceImpl;
import com.pinguela.yourpc.service.impl.ImageFileServiceImpl;
import com.pinguela.ypc.rest.api.util.AuthUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/image")
public class ImageResource {

	private static Logger logger = LogManager.getLogger(ImageResource.class);

	private CustomerService customerService;
	private ImageFileService imageFileService;

	public ImageResource() {
		this.customerService = new CustomerServiceImpl();
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
									mediaType = MediaType.APPLICATION_OCTET_STREAM,
									schema = @Schema(
											type = "string",
											format = "binary"
											)
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

	@GET
	@Path("/avatar")
	@Operation(
			method = "GET",
			operationId = "downloadAvatar",
			description = "Return user avatar", 
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully retrieved avatar",
							content = @Content(
									mediaType = MediaType.APPLICATION_OCTET_STREAM,
									schema = @Schema(
											type = "string",
											format = "binary"
											)
									)
							), 
					@ApiResponse(
							responseCode = "404",
							description = "Avatar not found"
							)
			})
	@SecurityRequirement(name = "bearerAuth")
	public Response downloadAvatar(
			@Context ContainerRequestContext context
			) {

		try {
			String sessionToken = AuthUtils.getSessionToken(context);
			Customer c = this.customerService.findBySessionToken(sessionToken);

			List<InputStream> isList = imageFileService.getInputStreams("avatar", c.getId());

			if (isList.size() < 1) {
				return Response.status(Status.NOT_FOUND).build();
			}

			for (int i = 1; i < isList.size(); i++) {
				isList.get(i).close();
			}

			return Response.ok(isList.get(0)).build();
		} catch (ServiceException | DataException | IOException e) {
			logger.error(e);
			throw new WebApplicationException(Status.BAD_REQUEST);
		}

	}

	@POST
	@Path("/avatar")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Operation(
			method = "POST",
			operationId = "uploadAvatar",
			description = "Upload user avatar",	
			requestBody = @RequestBody(
					content = @Content(
							mediaType = MediaType.MULTIPART_FORM_DATA,
							schemaProperties = @SchemaProperty(
									name = "file",
									schema = @Schema(
											type = "string",
											format = "binary"
											)
									)
							)
					),
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully uploaded avatar"
							),
					@ApiResponse(
							responseCode = "400",
							description = "Error in request"
							)
			})
	@SecurityRequirement(name = "bearerAuth")
	public Response uploadAvatar(
			@FormDataParam("file") @NotNull InputStream is,
			@Context ContainerRequestContext context
			) {

		try {
			String sessionToken = AuthUtils.getSessionToken(context);
			Customer c = this.customerService.findBySessionToken(sessionToken);

			BufferedImage img = ImageIO.read(is);
			ImageEntry entry = new ImageEntry(img, null);

			List<String> paths = imageFileService.getFilePaths("avatar", c.getId());
			if (!paths.isEmpty()) {
				entry.setPath(paths.get(0));
			}

			if (this.imageFileService.update("avatar", c.getId(), entry)) {
				return Response.ok().build();
			} else {
				return Response.status(Status.BAD_REQUEST).build();
			}
		} catch (ServiceException | DataException | IOException e) {
			logger.error(e);
			throw new WebApplicationException(Status.BAD_REQUEST);
		}

	}

}
