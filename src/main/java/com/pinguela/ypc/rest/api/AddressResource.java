package com.pinguela.ypc.rest.api;

import com.pinguela.yourpc.model.Address;
import com.pinguela.yourpc.service.AddressService;
import com.pinguela.yourpc.service.impl.AddressServiceImpl;
import com.pinguela.ypc.rest.api.constants.Roles;
import com.pinguela.ypc.rest.api.model.AddressDTOMixin;
import com.pinguela.ypc.rest.api.util.ResponseWrapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Path("/")
@RolesAllowed({Roles.ADMIN, Roles.HR})
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "address")
public class AddressResource {

	private AddressService addressService;

	public AddressResource() {
		this.addressService = new AddressServiceImpl();
	}

	@GET
	@Path("/address/{addressId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			method = "GET",
			operationId = "findAddressById",
			description = "Retrieve an address",
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully retrieved address",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(implementation = AddressDTOMixin.class)
									)
							),
					@ApiResponse(
							responseCode = "400",
							description = "Malformed path parameter"
							),
					@ApiResponse(
							responseCode = "401",
							description = "Caller is unauthenticated"
							),
					@ApiResponse(
							responseCode = "404",
							description = "No address found"
							)
			})
	public Response findById(@PathParam("addressId") Integer addressId) {
		return ResponseWrapper.wrap(() -> this.addressService.findById(addressId));
	}

	@POST
	@Path("/address")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			method = "POST",
			operationId = "createAddress",
			description = "Create an address",
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully created address",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(implementation = AddressDTOMixin.class)
									)
							),
					@ApiResponse(
							responseCode = "400",
							description = "Malformed parameter(s)"
							),
					@ApiResponse(
							responseCode = "401",
							description = "Caller is unauthenticated"
							)
			})
	public Response create(
			@FormParam("name") String name,
			@FormParam("customerId") Integer customerId,
			@FormParam("employeeId") Integer employeeId,
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
					Address a = new Address();
					a.setName(name);
					a.setCustomerId(customerId);
					a.setEmployeeId(employeeId);
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
	@Path("/address/{addressId}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			method = "PUT",
			operationId = "updateAddress",
			description = "Update an address",
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully updated address. If it was associated with an order, the ID is updated",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(implementation = AddressDTOMixin.class)
									)
							),
					@ApiResponse(
							responseCode = "400",
							description = "Malformed parameter(s)"
							),
					@ApiResponse(
							responseCode = "401",
							description = "Caller is unauthenticated"
							)
			})
	public Response update(
			@PathParam("id") @NotNull Integer id,
			@FormParam("name") String name,
			@FormParam("streetName") @NotNull String streetName,
			@FormParam("streetNumber") Short streetNumber,
			@FormParam("floor") Short floor,
			@FormParam("door") String door,
			@FormParam("zipCode") @NotNull String zipCode,
			@FormParam("cityId") @NotNull Integer cityId,
			@FormParam("isDefault") @NotNull Boolean isDefault,
			@FormParam("isBilling") @NotNull Boolean isBilling
			) {
		return ResponseWrapper.wrap(
				() -> {
					Address a = new Address();
					a.setId(id);
					a.setName(name);
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
	@Path("/address/{addressId}")
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
							description = "Malformed parameter(s)"
							),
					@ApiResponse(
							responseCode = "401",
							description = "Caller is unauthenticated"
							)
			})
	public Response delete(
			@PathParam("id") Integer id
			) {
		return ResponseWrapper.wrap(() -> this.addressService.delete(id));
	}

	@GET
	@Path("/customer/{customerId}/addresses")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			method = "GET",
			operationId = "findAddressesByCustomer",
			description = "Retrieve all address for the given customer",
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully retrieved addresses",
							content = @Content(
									mediaType = "application/json",
									array = @ArraySchema(
											schema = @Schema(implementation = AddressDTOMixin.class)
											)
									)
							),
					@ApiResponse(
							responseCode = "400",
							description = "Malformed path parameter"
							),
					@ApiResponse(
							responseCode = "401",
							description = "Caller is unauthenticated"
							)
			})
	public Response findByCustomer(
			@PathParam("customerId") Integer customerId
			) {
		return ResponseWrapper.wrap(
				() -> {
					return this.addressService.findByCustomer(customerId);
				},
				Status.OK, 
				Status.INTERNAL_SERVER_ERROR
				);
	}

	@DELETE
	@Path("/customer/{customerId}/addresses")
	@Operation(
			method = "DELETE",
			operationId = "deleteAddressesByCustomer",
			description = "Delete all address for the given customer",
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully deleted addresses"
							),
					@ApiResponse(
							responseCode = "400",
							description = "Malformed path parameter"
							),
					@ApiResponse(
							responseCode = "401",
							description = "Caller is unauthenticated"
							)
			})
	public Response deleteByCustomer(
			@PathParam("customerId") Integer customerId
			) {
		return ResponseWrapper.wrap(
				() -> {
					return this.addressService.deleteByCustomer(customerId);
				});
	}

}
