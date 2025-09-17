package com.pinguela.ypc.rest.api;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.pinguela.DataException;
import com.pinguela.InvalidLoginCredentialsException;
import com.pinguela.ServiceException;
import com.pinguela.YPCException;
import com.pinguela.yourpc.model.Address;
import com.pinguela.yourpc.model.Customer;
import com.pinguela.yourpc.model.CustomerOrder;
import com.pinguela.yourpc.model.ImageEntry;
import com.pinguela.yourpc.model.OrderLine;
import com.pinguela.yourpc.model.RMA;
import com.pinguela.yourpc.model.RMACriteria;
import com.pinguela.yourpc.model.Ticket;
import com.pinguela.yourpc.model.TicketCriteria;
import com.pinguela.yourpc.model.TicketMessage;
import com.pinguela.yourpc.model.dto.ProductDTO;
import com.pinguela.yourpc.service.AddressService;
import com.pinguela.yourpc.service.CustomerOrderService;
import com.pinguela.yourpc.service.CustomerService;
import com.pinguela.yourpc.service.ImageFileService;
import com.pinguela.yourpc.service.ProductService;
import com.pinguela.yourpc.service.RMAService;
import com.pinguela.yourpc.service.TicketMessageService;
import com.pinguela.yourpc.service.TicketService;
import com.pinguela.yourpc.service.impl.AddressServiceImpl;
import com.pinguela.yourpc.service.impl.CustomerOrderServiceImpl;
import com.pinguela.yourpc.service.impl.CustomerServiceImpl;
import com.pinguela.yourpc.service.impl.ImageFileServiceImpl;
import com.pinguela.yourpc.service.impl.ProductServiceImpl;
import com.pinguela.yourpc.service.impl.RMAServiceImpl;
import com.pinguela.yourpc.service.impl.TicketMessageServiceImpl;
import com.pinguela.yourpc.service.impl.TicketServiceImpl;
import com.pinguela.ypc.rest.api.constants.Roles;
import com.pinguela.ypc.rest.api.model.AddressDTOMixin;
import com.pinguela.ypc.rest.api.model.CustomerDTOMixin;
import com.pinguela.ypc.rest.api.model.ErrorLog;
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
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
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
			description = "Caller lacks sufficient permissions to access the endpoint"
			)
})
public class MeResource {

	private static Logger logger = LogManager.getLogger(MeResource.class);

	private ProductService productService;
	private CustomerService customerService;
	private AddressService addressService;
	private CustomerOrderService orderService;
	private ImageFileService imageFileService;
	private TicketService ticketService;
	private TicketMessageService ticketMessageService;
	private RMAService rmaService;

	@Context 
	private SecurityContext securityContext;

	public MeResource() {
		productService = new ProductServiceImpl();
		customerService = new CustomerServiceImpl();
		addressService = new AddressServiceImpl();
		orderService = new CustomerOrderServiceImpl();
		imageFileService = new ImageFileServiceImpl();
		ticketService = new TicketServiceImpl();
		ticketMessageService = new TicketMessageServiceImpl();
		rmaService = new RMAServiceImpl();

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

	@PUT
	@Path("/")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Operation(
			method = "PUT",
			operationId = "updateMe",
			description = "Updates the authenticated customer's data", 
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully updated customer data",
							content = @Content(
									schema = @Schema(
											implementation = CustomerDTOMixin.class
											)
									)
							),
					@ApiResponse(
							responseCode = "400",
							description = "Malformed request",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(implementation = ErrorLog.class)
									)
							)
			})
	public Response update(
			@FormParam("firstName") @NotNull String firstName,
			@FormParam("lastName1") @NotNull String lastName1,
			@FormParam("lastName2") String lastName2,
			@FormParam("docType") @NotNull String documentTypeId,
			@FormParam("docNumber") @NotNull String documentNumber,
			@FormParam("phoneNumber") @NotNull String phoneNumber
			) {
		Integer customerId = AuthUtils.getUserId(securityContext);

		Customer customer = new Customer();
		customer.setId(customerId);
		customer.setFirstName(firstName);
		customer.setLastName1(lastName1);
		customer.setLastName2(lastName2);
		customer.setDocumentTypeId(documentTypeId);
		customer.setDocumentNumber(documentNumber);
		customer.setPhoneNumber(phoneNumber);

		return ResponseWrapper.wrap(() -> {
			customerService.update(customer);
			return customerService.findById(customerId);
		});
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
			@FormParam("oldPassword") String oldPassword,
			@FormParam("newPassword") String newPassword
			) {
		Integer customerId = AuthUtils.getUserId(securityContext);

		try {
			// TODO: This probably should have a dedicated method in business logic
			Customer c = this.customerService.findById(customerId);
			this.customerService.login(c.getEmail(), oldPassword);

			this.customerService.updatePassword(customerId, newPassword);
		} catch (InvalidLoginCredentialsException e) {
			logger.error(e);
			throw new WebApplicationException(Status.BAD_REQUEST);
		} catch (YPCException e) {
			logger.error(e);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		} 

		return Response.status(Status.NO_CONTENT).build();
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

	@GET
	@Path("/tickets/{locale}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			method = "GET",
			operationId = "getMyTickets",
			description = "Retrieve a paginated list of tickets for the authenticated customer based on the provided criteria",
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully retrieved tickets",
							content = @Content(
									mediaType = MediaType.APPLICATION_JSON,
									array = @ArraySchema(
											schema = @Schema(implementation = Ticket.class)
											)
									)
							)
			})
	public Response findTickets(
			@PathParam("locale") String locale,
			@QueryParam("dateFrom") Date dateFrom,
			@QueryParam("dateTo") Date dateTo,
			@QueryParam("state") String state,
			@QueryParam("page") @DefaultValue("1") Integer page,
			@QueryParam("pageSize") @DefaultValue("10") Integer pageSize
			) {
		Integer customerId = AuthUtils.getUserId(securityContext);
		Locale l = LocaleUtils.getLocale(locale);

		TicketCriteria criteria = new TicketCriteria();
		criteria.setCustomerId(customerId);
		criteria.setMinDate(dateFrom);
		criteria.setMaxDate(dateTo);
		criteria.setState(state);

		return ResponseWrapper.wrap(() -> this.ticketService.findBy(criteria, l, page, pageSize));
	}

	@POST
	@Path("/tickets")
	@Consumes({MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			method = "POST",
			operationId = "createMyTicket",
			description = "Open a ticket as the authenticated customer",
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully opened ticket",
							content = @Content(
									mediaType = MediaType.APPLICATION_JSON,
									schema = @Schema(implementation = Ticket.class)
									)
							),
					@ApiResponse(
							responseCode = "400",
							description = "Malformed parameter(s) in request"
							)
			})
	public Response createTicket(
			@FormParam("productId") Integer productId,
			@FormParam("title") @NotNull String title,
			@FormParam("description") @NotNull String description,
			@FormParam("orderLines")
			@Parameter(
					schema = @Schema(
							type = "string"
							)
					)
			List<OrderLine> orderLines
			) {
		Integer customerId = AuthUtils.getUserId(securityContext);

		Ticket t = new Ticket();
		t.setCustomerId(customerId);
		t.setTitle(title);
		t.setDescription(description);
		t.setOrderLines(orderLines);

		return ResponseWrapper.wrap(() -> this.ticketService.create(t));
	}

	@POST
	@Path("/tickets/{locale}/{ticketId}/messages")
	@Consumes({MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			method = "POST",
			operationId = "createMyTicketMessage",
			description = "Reply to a ticket as the authenticated customer",
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully opened ticket",
							content = @Content(
									mediaType = MediaType.APPLICATION_JSON,
									schema = @Schema(implementation = Ticket.class)
									)
							),
					@ApiResponse(
							responseCode = "400",
							description = "Malformed parameter(s) in request"
							)
			})
	public Response createTicketMessage(
			@PathParam("locale") String locale,
			@PathParam("ticketId") Long ticketId,
			@FormParam("message") @NotNull String message
			) {
		return ResponseWrapper.wrap(() -> {
			Integer customerId = AuthUtils.getUserId(securityContext);
			Locale l = LocaleUtils.getLocale(locale);
			Ticket t = this.ticketService.findById(ticketId, l);

			if (!t.getCustomerId().equals(customerId)) {
				throw new WebApplicationException(Status.UNAUTHORIZED);
			}

			TicketMessage tm = new TicketMessage();
			tm.setTicketId(ticketId);
			tm.setCustomerId(customerId);
			tm.setText(message);

			ticketMessageService.create(tm);
			return ticketService.findById(ticketId, l);
		});
	}

	@GET
	@Path("/rma/{locale}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			method = "GET",
			operationId = "getMyRma",
			description = "Retrieve a list of RMA for the authenticated customer based on the provided criteria",
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully retrieved tickets",
							content = @Content(
									mediaType = MediaType.APPLICATION_JSON,
									array = @ArraySchema(
											schema = @Schema(implementation = RMA.class)
											)
									)
							)
			})
	public Response findRma(
			@PathParam("locale") String locale,
			@QueryParam("orderId") Long orderId,
			@QueryParam("dateFrom") Date dateFrom,
			@QueryParam("dateTo") Date dateTo,
			@QueryParam("state") String state
			) {
		Integer customerId = AuthUtils.getUserId(securityContext);
		Locale l = LocaleUtils.getLocale(locale);

		RMACriteria criteria = new RMACriteria();
		criteria.setCustomerId(customerId);
		criteria.setOrderId(orderId);
		criteria.setMinDate(dateFrom);
		criteria.setMaxDate(dateTo);
		criteria.setState(state);

		return ResponseWrapper.wrap(() -> this.rmaService.findBy(criteria, l));
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
							description = "Malformed parameter(s) in request"
							)
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
