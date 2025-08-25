package com.pinguela.ypc.rest.api.util;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

public class AuthUtils {
	
	public static String getSessionToken(ContainerRequestContext requestContext) {
		
		String auth = requestContext.getHeaderString("Authorization");
		
		if (auth == null || !auth.startsWith("Bearer ")) {
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST).build());
		}
		
		return auth.substring(7);
	}

}
