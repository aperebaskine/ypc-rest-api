package com.pinguela.ypc.rest.api;

import com.pinguela.ypc.rest.api.annotations.Public;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Public
@Path("/openapi.{type:json|yaml}")
@Produces(MediaType.APPLICATION_JSON)
public class OpenApiResource extends io.swagger.v3.jaxrs2.integration.resources.OpenApiResource {

}
