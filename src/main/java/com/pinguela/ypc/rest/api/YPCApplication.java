package com.pinguela.ypc.rest.api;

import org.glassfish.jersey.server.ResourceConfig;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.ws.rs.ApplicationPath;

@OpenAPIDefinition(
		servers = {
				@Server(url = "http://localhost:8080/ypc-rest-api/", description = "REST API SERVER")
		}
		)
@ApplicationPath("/api")
public class YPCApplication extends ResourceConfig {

	public YPCApplication() {
		packages(YPCApplication.class.getPackage().getName());
		register(io.swagger.v3.jaxrs2.integration.resources.OpenApiResource.class);
	}

}
