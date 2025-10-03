package com.pinguela.ypc.rest.api.filter;

import java.io.IOException;

import org.glassfish.jersey.http.HttpHeaders;

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

		String origin = requestContext.getHeaderString(HttpHeaders.ORIGIN);

		if (origin == null) {
			return;
		}

		MultivaluedMap<String, Object> headers = responseContext.getHeaders();

		headers.putSingle("Access-Control-Allow-Origin", origin);
		headers.putSingle("Access-Control-Allow-Headers",
				"CSRF-Token, X-Requested-By, Authorization, Content-Type");
		headers.putSingle("Access-Control-Allow-Credentials", "true");
		headers.putSingle("Access-Control-Allow-Methods",
				"GET, POST, PUT, PATCH, DELETE, OPTIONS, HEAD");
	}

}
