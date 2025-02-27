package com.pinguela.ypc.rest.api.filter;

import java.io.IOException;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CORSFilter implements ContainerResponseFilter {

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {
		
		MultivaluedMap<String, Object> headers = responseContext.getHeaders();
		
		// TODO: Dynamically resolve allowed origins
		headers.add("Access-Control-Allow-Origin", "http://localhost:4200");
        headers.add("Access-Control-Allow-Headers",
                "CSRF-Token, X-Requested-By, Authorization, Content-Type");
        headers.add("Access-Control-Allow-Credentials", "true");
        headers.add("Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS, HEAD");
	}
	
}
