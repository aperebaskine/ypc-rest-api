package com.pinguela.ypc.rest.api;

import org.glassfish.jersey.server.ResourceConfig;

import com.pinguela.ypc.rest.api.filter.CORSFilter;
import com.pinguela.ypc.rest.api.json.ObjectMapperContextResolver;
import com.pinguela.ypc.rest.api.json.param.AttributeParamConverterProvider;
import com.pinguela.ypc.rest.api.mixin.LightAttributeDTOMixin;
import com.pinguela.ypc.rest.api.schema.AttributeValueModelConverter;

import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
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
@ApplicationPath("/api")
public class YPCApplication extends ResourceConfig {

	public YPCApplication() {
		// Resource package
		packages(YPCApplication.class.getPackage().getName());

		// Filter package
		packages(CORSFilter.class.getPackage().getName());

		// Swagger UI openapi.json resource
		register(io.swagger.v3.jaxrs2.integration.resources.OpenApiResource.class);
		register(LightAttributeDTOMixin.class);

		// Attribute value schema creator
		ModelConverters.getInstance().addConverter(new AttributeValueModelConverter());

		// Jackson serialization-related classes
		register(ObjectMapperContextResolver.class);
		register(AttributeParamConverterProvider.class);
	}

}
