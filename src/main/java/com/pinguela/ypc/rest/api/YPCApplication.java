package com.pinguela.ypc.rest.api;

import org.glassfish.jersey.server.ResourceConfig;

import com.pinguela.ypc.rest.api.schema.AttributeValueModelConverter;

import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.ws.rs.ApplicationPath;

@OpenAPIDefinition(
		info = @Info(
				title = "ypc-rest-api",
				version = "0.0.1"
				),
		servers = {
				@Server(
						url = "http://localhost:8080/ypc-rest-api/", 
						description = "REST API Server for YPC"
						)
		})
@SecurityScheme(
		name = "bearerAuth",
		type = SecuritySchemeType.HTTP,
		scheme = "bearer"
		)
@ApplicationPath("/api")
public class YPCApplication extends ResourceConfig {

	public YPCApplication() {
		// Resource package
		packages(YPCApplication.class.getPackage().getName());

		// Swagger UI openapi.json resource
		register(io.swagger.v3.jaxrs2.integration.resources.OpenApiResource.class);

		// Attribute value schema creator
		ModelConverters.getInstance().addConverter(new AttributeValueModelConverter());
	}

}
