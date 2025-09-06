package com.pinguela.ypc.rest.api.filter;

import java.io.IOException;
import java.security.Principal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pinguela.ypc.rest.api.exception.InvalidTokenException;
import com.pinguela.ypc.rest.api.model.UserPrincipal;
import com.pinguela.ypc.rest.api.util.TokenManager;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {
	
	private static Logger logger = LogManager.getLogger(AuthenticationFilter.class);
	private TokenManager tokenManager = TokenManager.getInstance();

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		String auth = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
		
		if (auth == null) {
			return;
		}

		if (!auth.startsWith("Bearer ")) {
			throw new WebApplicationException(Response.status(Status.UNAUTHORIZED).build());
		}

		String token = auth.substring(7).trim();
		UserPrincipal user;
		
		try {
			user = tokenManager.decodeToken(token);
		} catch (InvalidTokenException e) {
			logger.warn(e);
			throw new WebApplicationException(Response.status(Status.UNAUTHORIZED).build());
		}
		
		SecurityContext context = requestContext.getSecurityContext();
		
		requestContext.setSecurityContext(new SecurityContext() {
			
			@Override
			public boolean isUserInRole(String role) {
				return user.getRole().equals(role);
			}
			
			@Override
			public boolean isSecure() {
				return context.isSecure();
			}
			
			@Override
			public Principal getUserPrincipal() {
				return user;
			}
			
			@Override
			public String getAuthenticationScheme() {
				return context.getAuthenticationScheme();
			}
		});
	}

}
