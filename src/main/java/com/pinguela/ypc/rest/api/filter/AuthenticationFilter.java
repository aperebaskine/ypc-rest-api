package com.pinguela.ypc.rest.api.filter;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pinguela.ypc.rest.api.annotations.Public;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {
	
	private static Logger logger = LogManager.getLogger(AuthenticationFilter.class);
	
	@Context
	private ResourceInfo resourceInfo;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		
		if (resourceInfo.getResourceClass().isAnnotationPresent(Public.class)
				|| resourceInfo.getResourceMethod().isAnnotationPresent(Public.class)) {
			return;
		}

		String auth = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
		
		if (auth == null) {
			logger.warn("An unauthenticated user attempted to call an auth-gated endpoint. Public endpoints must declare the @Public annotation.");
			throw new WebApplicationException(Response.status(Status.UNAUTHORIZED).build());
		}
	}

}
