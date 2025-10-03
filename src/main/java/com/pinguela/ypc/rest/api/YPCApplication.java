package com.pinguela.ypc.rest.api;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.server.validation.ValidationFeature;

import com.pinguela.ypc.rest.api.schema.AttributeValueModelConverter;

import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.ApplicationPath;

@OpenAPIDefinition(
		info = @Info(
				title = "ypc-rest-api",
				version = "0.0.1"
				),
		servers = {
				@Server(
						url = "https://informaticapinguela.es/ypc-rest-api/", 
						description = "Production server"
						),
				@Server(
						url = "http://localhost:8080/ypc-rest-api/", 
						description = "Local testing server"
						),
				@Server(
						url = "http://localhost:4200/ypc-rest-api/", 
						description = "Angular proxy for local testing server"
						)
		},
		tags = {
				@Tag(name = "session", description = "Endpoints related to managing the authenticated user's session"),
				@Tag(name = "product", description = "Endpoints for requests handling logic related to products"),
				@Tag(name = "customer", description = "Endpoints for requests handling logic related to customers"),
				@Tag(name = "order", description = "Endpoints for requests handling logic related to orders. Allowed roles: admin, support"),
				@Tag(name = "me", description = "Endpoints for customer-facing application. Allowed roles: customer"),
				@Tag(name = "address", description = "Endpoints for requests handling logic related to addresses. Allowed roles: admin, hr"),
				@Tag(name = "geo", description = "Endpoints for retrieving data about administrative units of supported countries"),
				@Tag(name = "identity", description = "Endpoints for retrieving data used for user identification")
		})
@SecurityScheme(
		name = "bearerAuth",
		type = SecuritySchemeType.HTTP,
		scheme = "bearer",
		bearerFormat = "JWT"
		)
@ApplicationPath("/api")
public class YPCApplication extends ResourceConfig {

	public YPCApplication() {
		// Resource package
		packages(YPCApplication.class.getPackage().getName());

		// Swagger UI openapi.json resource
		register(OpenApiResource.class);
		
		// RBAC enforcer
		register(RolesAllowedDynamicFeature.class);
		
		// Parameter validator
		register(ValidationFeature.class);

		// Attribute value schema creator
		ModelConverters.getInstance().addConverter(new AttributeValueModelConverter());
	}

}
