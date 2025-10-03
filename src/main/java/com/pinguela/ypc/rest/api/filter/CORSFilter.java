package com.pinguela.ypc.rest.api.filter;

import java.io.IOException;
import java.net.URI;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.HEADER_DECORATOR)
public class CORSFilter implements ContainerResponseFilter {

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {

		MultivaluedMap<String, Object> headers = responseContext.getHeaders();
		String origin = requestContext.getHeaderString(org.glassfish.jersey.http.HttpHeaders.ORIGIN);
		
		if (origin == null) {
			return;
		}
		
		URI baseUri = requestContext.getUriInfo().getBaseUri();
		URI originUri = URI.create(origin);

		if (baseUri.getHost().equals(originUri.getHost())) {
			headers.add("Access-Control-Allow-Origin", origin);
			headers.add("Access-Control-Allow-Headers",
					"CSRF-Token, X-Requested-By, Authorization, Content-Type");
			headers.add("Access-Control-Allow-Credentials", "true");
			headers.add("Access-Control-Allow-Methods",
					"GET, POST, PUT, PATCH, DELETE, OPTIONS, HEAD");
		}
	}

}
