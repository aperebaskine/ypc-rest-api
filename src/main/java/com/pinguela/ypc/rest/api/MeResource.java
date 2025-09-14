package com.pinguela.ypc.rest.api;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.pinguela.DataException;
import com.pinguela.ServiceException;
import com.pinguela.YPCException;
import com.pinguela.yourpc.model.Address;
import com.pinguela.yourpc.model.Customer;
import com.pinguela.yourpc.model.CustomerOrder;
import com.pinguela.yourpc.model.ImageEntry;
import com.pinguela.yourpc.model.OrderLine;
import com.pinguela.yourpc.model.dto.ProductDTO;
import com.pinguela.yourpc.service.AddressService;
import com.pinguela.yourpc.service.CustomerOrderService;
import com.pinguela.yourpc.service.CustomerService;
import com.pinguela.yourpc.service.ImageFileService;
import com.pinguela.yourpc.service.ProductService;
import com.pinguela.yourpc.service.impl.AddressServiceImpl;
import com.pinguela.yourpc.service.impl.CustomerOrderServiceImpl;
import com.pinguela.yourpc.service.impl.CustomerServiceImpl;
import com.pinguela.yourpc.service.impl.ImageFileServiceImpl;
import com.pinguela.yourpc.service.impl.ProductServiceImpl;
import com.pinguela.ypc.rest.api.constants.Roles;
import com.pinguela.ypc.rest.api.model.AddressDTOMixin;
import com.pinguela.ypc.rest.api.model.CustomerDTOMixin;
import com.pinguela.ypc.rest.api.util.AuthUtils;
import com.pinguela.ypc.rest.api.util.LocaleUtils;
import com.pinguela.ypc.rest.api.util.ResponseWrapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
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
import jakarta.ws.rs.core.SecurityContext;

@Path("/me")
@RolesAllowed(Roles.CUSTOMER)
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "me")
@ApiResponses({
	@ApiResponse(
			responseCode = "401",
			description = "Caller is unauthenticated"
			),
	@ApiResponse(
			responseCode = "403",
			description = "Caller lacks permissions to access the endpoint"
			)
})
public class MeResource {

	private static Logger logger = LogManager.getLogger(MeResource.class);

	private ProductService productService;
	private CustomerService customerService;
	private AddressService addressService;
	private CustomerOrderService orderService;
	private ImageFileService imageFileService;

	@Context 
	private SecurityContext securityContext;

	public MeResource() {
		productService = new ProductServiceImpl();
		customerService = new CustomerServiceImpl();
		addressService = new AddressServiceImpl();
		orderService = new CustomerOrderServiceImpl();
		imageFileService = new ImageFileServiceImpl();
	}

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			method = "GET",
			operationId = "getMe",
			description = "Retrieve the currently authenticated customer's data", 
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully retrieved customer data",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(implementation = CustomerDTOMixin.class)
									)
							)
			})
	public Response find() {
		Integer customerId = AuthUtils.getUserId(securityContext);
		return ResponseWrapper.wrap(() -> customerService.findById(customerId), Status.NOT_FOUND);
	}

	@GET
	@Path("/addresses")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			method = "GET",
			operationId = "getMyAddresses",
			description = "Retrieve all address for the authenticated customer.",
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully retrieved addresses",
							content = @Content(
									mediaType = MediaType.APPLICATION_JSON,
									array = @ArraySchema(
											schema = @Schema(implementation = AddressDTOMixin.class)
											)
									)
							)
			})
	public Response findAddresses() {
		Integer customerId = AuthUtils.getUserId(securityContext);
		return ResponseWrapper.wrap(() -> this.addressService.findByCustomer(customerId), Status.OK);
	}

	@POST
	@Path("/addresses")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			method = "POST",
			operationId = "createMyAddress",
			description = "Create an address for the authenticated customer",
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
							)
			})
	public Response createAddress(
			@FormParam("name") @NotNull String name,
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

					a.setName(name);
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
	@Path("/addresses/{addressId}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			method = "PUT",
			operationId = "updateMyAddress",
			description = "Update an address for the authenticated customer",
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
							)
			})
	public Response updateAddress(
			@PathParam("id") @NotNull Integer id,
			@FormParam("name") @NotNull String name,
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
					Integer customerId = AuthUtils.getUserId(securityContext);
					Address current = this.addressService.findById(customerId);

					if (current.getCustomerId().equals(customerId)) {
						throw new WebApplicationException(Status.FORBIDDEN);
					}

					Address a = new Address();
					a.setId(id);
					a.setName(name);
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

	@GET
	@Path("/orders/{locale}/{orderId}")
	@Operation(
			method = "GET",
			operationId = "getMyOrders",
			description = "Retrieve the authenticated customer's orders",
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully retrieved orders",
							content = @Content(
									mediaType = "application/json",
									array = @ArraySchema(
											schema = @Schema(implementation = CustomerOrder.class)
											)
									)
							),
					@ApiResponse(
							responseCode = "400",
							description = "Malformed parameter(s) in request"
							)
			})
	public Response findOrder(
			@PathParam("locale") String locale,
			@PathParam("orderId") Long orderId
			) {
		Integer customerId = AuthUtils.getUserId(securityContext);
		Locale l = LocaleUtils.getLocale(locale);


		return ResponseWrapper.wrap(() -> {
			CustomerOrder order = orderService.findById(orderId, l);

			if (!order.getCustomerId().equals(customerId)) {
				throw new WebApplicationException(Status.FORBIDDEN);
			}

			return order;
		});
	}

	@GET
	@Path("/orders/{locale}")
	@Operation(
			method = "GET",
			operationId = "getMyOrders",
			description = "Retrieve the authenticated customer's orders",
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully retrieved orders",
							content = @Content(
									mediaType = "application/json",
									array = @ArraySchema(
											schema = @Schema(implementation = CustomerOrder.class)
											)
									)
							),
					@ApiResponse(
							responseCode = "400",
							description = "Malformed parameter(s) in request"
							)
			})
	public Response findOrders(
			@PathParam("locale") String locale
			) {
		Integer customerId = AuthUtils.getUserId(securityContext);
		Locale l = LocaleUtils.getLocale(locale);
		return ResponseWrapper.wrap(() -> orderService.findByCustomer(customerId, l));
	}

	@POST
	@Path("/orders")
	@Consumes({MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			method = "POST",
			operationId = "createMyOrder",
			description = "Place an order as the authenticated customer",
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully placed order",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(implementation = CustomerOrder.class)
									)
							),
					@ApiResponse(
							responseCode = "400",
							description = "Malformed parameter(s) in request"
							)
			})
	public Response createOrder(
			@FormParam("billingAddressId") @NotNull Integer billingAddressId,
			@FormParam("shippingAddressId") @NotNull Integer shippingAddressId,
			@FormParam("orderLines") @NotNull
			@Parameter(
					schema = @Schema(
							type = "string"
							)
					)
			List<OrderLine> orderLines
			) {

		for (OrderLine ol: orderLines) {
			try {
				ProductDTO p = this.productService.findById(ol.getProductId().longValue(),
						LocaleUtils.getDefault());
				ol.setPurchasePrice(p.getPurchasePrice());
			} catch (Exception e) {
				throw new WebApplicationException(Status.BAD_REQUEST);

			}
		}

		try {
			Integer customerId = AuthUtils.getUserId(securityContext);
			Customer c = this.customerService.findById(customerId);

			Address billing = this.addressService.findById(billingAddressId);
			Address shipping = this.addressService.findById(shippingAddressId);

			if (!billing.getCustomerId().equals(c.getId())
					|| !shipping.getCustomerId().equals(c.getId())) {
				throw new WebApplicationException(Status.BAD_REQUEST);
			}

			CustomerOrder co = new CustomerOrder();
			co.setCustomerId(c.getId());
			co.setBillingAddressId(billingAddressId);
			co.setShippingAddressId(shippingAddressId);
			co.setOrderLines(orderLines);

			co.setState("PND");

			Double total = co.getOrderLines().stream()
					.map((orderLine) -> orderLine.getQuantity() * orderLine.getSalePrice())
					.reduce((t, u) -> t + u)
					.get();

			co.setTotalPrice(total);

			Long id = this.orderService.create(co);

			if (id == null) {
				throw new WebApplicationException(Status.BAD_REQUEST);
			}

			co.setId(id);
			return Response.ok(co).build();

		} catch (ServiceException | DataException e) {
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}
	}

	@PATCH
	@Path("/password")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Operation(
			method = "PATCH",
			operationId = "updateMyPassword",
			description = "Update the authenticated customer's password",
			responses = {
					@ApiResponse(
							responseCode = "204", 
							description = "Successfully updated password"
							),
					@ApiResponse(
							responseCode = "400",
							description = "Malformed password"
							)
			})
	public Response updatePassword(
			@FormParam("password") String password
			) {
		Integer customerId = AuthUtils.getUserId(securityContext);

		try {
			this.customerService.updatePassword(customerId, password);
		} catch (YPCException e) {
			logger.error(e);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		} 

		return Response.status(Status.NO_CONTENT).build();
	}

	@GET
	@Path("/avatar")
	@Operation(
			method = "GET",
			operationId = "getMyAvatar",
			description = "Return the authenticated user's avatar", 
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
	public Response downloadAvatar(
			@Context ContainerRequestContext context
			) {

		try {
			Integer userId = AuthUtils.getUserId(context);
			List<InputStream> isList = imageFileService.getInputStreams("avatar", userId);

			if (isList.size() < 1) {
				return Response.status(Status.NOT_FOUND).build();
			}

			for (int i = 1; i < isList.size(); i++) {
				isList.get(i).close();
			}

			return Response.ok(isList.get(0)).build();
		} catch (ServiceException | IOException e) {
			logger.error(e);
			throw new WebApplicationException(Status.BAD_REQUEST);
		}

	}

	@POST
	@Path("/avatar")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Operation(
			method = "POST",
			operationId = "uploadMyAvatar",
			description = "Upload the authenticated user's avatar",	
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
							),

			})
	public Response uploadAvatar(
			@FormDataParam("file") @NotNull InputStream is,
			@Context ContainerRequestContext context
			) {

		try {
			Integer userId = AuthUtils.getUserId(context);

			BufferedImage img = ImageIO.read(is);
			ImageEntry entry = new ImageEntry(img, null);

			List<String> paths = imageFileService.getFilePaths("avatar", userId);
			if (!paths.isEmpty()) {
				entry.setPath(paths.get(0));
			}

			if (this.imageFileService.update("avatar", userId, entry)) {
				return Response.ok().build();
			} else {
				return Response.status(Status.BAD_REQUEST).build();
			}
		} catch (ServiceException | IOException e) {
			logger.error(e);
			throw new WebApplicationException(Status.BAD_REQUEST);
		}

	}

}
