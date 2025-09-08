package com.pinguela.ypc.rest.api;

import com.pinguela.yourpc.model.Address;
import com.pinguela.yourpc.service.AddressService;
import com.pinguela.yourpc.service.impl.AddressServiceImpl;
import com.pinguela.ypc.rest.api.util.AuthUtils;
import com.pinguela.ypc.rest.api.util.ResponseWrapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/address")
@RolesAllowed("admin")
@SecurityRequirement(name = "bearerAuth")
public class AddressResource {

	private AddressService addressService;

	public AddressResource() {
		this.addressService = new AddressServiceImpl();
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			method = "GET",
			operationId = "findAddressById",
			description = "Retrieve an address.",
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully retrieved address",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(implementation = Address.class)
									)
							),
					@ApiResponse(
							responseCode = "400",
							description = "Error in parameter"
							)
			})
	public Response findById(
			@PathParam("id") Integer id,
			@Context ContainerRequestContext context
			) {
		return ResponseWrapper.wrap(
				() -> {
					Integer customerId = AuthUtils.getUserId(context);
					Address a = this.addressService.findById(id);

					if (!a.getCustomerId().equals(customerId)) {
						throw new WebApplicationException(Status.BAD_REQUEST);
					}

					return a;
				}
				);
	}

	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			method = "POST",
			operationId = "createAddress",
			description = "Create an address.",
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully created address",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(implementation = Address.class)
									)
							),
					@ApiResponse(
							responseCode = "400",
							description = "Error in parameter"
							)
			})
	public Response create(
			@FormParam("streetName") @NotNull String streetName,
			@FormParam("streetNumber") Short streetNumber,
			@FormParam("floor") Short floor,
			@FormParam("door") String door,
			@FormParam("zipCode") @NotNull String zipCode,
			@FormParam("cityId") @NotNull Integer cityId,
			@FormParam("isDefault") @NotNull Boolean isDefault,
			@FormParam("isBilling") @NotNull Boolean isBilling,
			@Context ContainerRequestContext context
			) {
		return ResponseWrapper.wrap(
				() -> {
					Integer customerId = AuthUtils.getUserId(context);

					Address a = new Address();
					a.setCustomerId(customerId);
					a.setStreetName(streetName);
					a.setStreetNumber(streetNumber);
					a.setFloor(floor);
					a.setDoor(door);
					a.setZipCode(zipCode);
					a.setCityId(cityId);
					a.setIsDefault(isDefault);
					a.setIsBilling(isBilling);

					Integer id = this.addressService.create(a);

					if (id == null) {
						throw new WebApplicationException(Status.BAD_REQUEST);
					}

					a.setId(id);
					return a;
				}
				);
	}

	@PUT
	@Path("/")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			method = "PUT",
			operationId = "updateAddress",
			description = "Update an address.",
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully updated address",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(implementation = Address.class)
									)
							),
					@ApiResponse(
							responseCode = "400",
							description = "Error in parameter"
							)
			})
	public Response update(
			@FormParam("id") @NotNull Integer id,
			@FormParam("streetName") @NotNull String streetName,
			@FormParam("streetNumber") Short streetNumber,
			@FormParam("floor") Short floor,
			@FormParam("door") String door,
			@FormParam("zipCode") @NotNull String zipCode,
			@FormParam("cityId") @NotNull Integer cityId,
			@FormParam("isDefault") @NotNull Boolean isDefault,
			@FormParam("isBilling") @NotNull Boolean isBilling,
			@Context ContainerRequestContext context
			) {
		return ResponseWrapper.wrap(
				() -> {
					Integer customerId = AuthUtils.getUserId(context);

					Address current = this.addressService.findById(id);

					if (!current.getCustomerId().equals(customerId)) {
						throw new WebApplicationException(Status.BAD_REQUEST);
					}

					Address a = new Address();
					a.setId(id);
					a.setCreationDate(current.getCreationDate());
					a.setCustomerId(customerId);
					a.setStreetName(streetName);
					a.setStreetNumber(streetNumber);
					a.setFloor(floor);
					a.setDoor(door);
					a.setZipCode(zipCode);
					a.setCityId(cityId);
					a.setIsDefault(isDefault);
					a.setIsBilling(isBilling);

					Integer newId = this.addressService.update(a);

					if (id == null) {
						throw new WebApplicationException(Status.BAD_REQUEST);
					}

					a.setId(newId);
					return a;
				}
				);
	}


	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			method = "DELETE",
			operationId = "deleteAddressById",
			description = "Delete an address.",
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully deleted address"
							),
					@ApiResponse(
							responseCode = "400",
							description = "Error in parameter"
							)
			})
	public Response delete(
			@PathParam("id") Integer id,
			@Context ContainerRequestContext context
			) {
		return ResponseWrapper.wrap(
				() -> {
					Integer customerId = AuthUtils.getUserId(context);
					Address a = this.addressService.findById(id);

					if (!a.getCustomerId().equals(customerId)) {
						throw new WebApplicationException(Status.BAD_REQUEST);
					}

					return this.addressService.delete(id);
				}
				);
	}

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			method = "GET",
			operationId = "findAllAddresses",
			description = "Retrieve all address for the given user.",
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully retrieved addresses",
							content = @Content(
									mediaType = "application/json",
									array = @ArraySchema(
											schema = @Schema(implementation = Address.class)
											)
									)
							),
					@ApiResponse(
							responseCode = "500",
							description = "Unknown error"
							)
			})
	public Response findAll(
			@Context ContainerRequestContext context
			) {
		return ResponseWrapper.wrap(
				() -> {
					Integer customerId = AuthUtils.getUserId(context);
					return this.addressService.findByCustomer(customerId);
				},
				Status.OK, 
				Status.INTERNAL_SERVER_ERROR
				);
	}

	@DELETE
	@Path("/")
	@Operation(
			method = "DELETE",
			operationId = "deleteAllAddresses",
			description = "Delete all address for the given user.",
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully deleted addresses"
							),
					@ApiResponse(
							responseCode = "500",
							description = "Unknown error"
							)
			})
	public Response deleteAll(
			@Context ContainerRequestContext context
			) {
		return ResponseWrapper.wrap(
				() -> {
					Integer customerId = AuthUtils.getUserId(context);
					return this.addressService.deleteByCustomer(customerId);
				});
	}

}
