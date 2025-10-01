package com.pinguela.ypc.rest.api.filter;

import java.io.IOException;
import java.lang.reflect.Method;
import java.security.Principal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pinguela.yourpc.service.cache.Cache;
import com.pinguela.yourpc.service.cache.CacheManager;
import com.pinguela.ypc.rest.api.annotations.Public;
import com.pinguela.ypc.rest.api.exception.InvalidTokenException;
import com.pinguela.ypc.rest.api.model.Session;
import com.pinguela.ypc.rest.api.model.UserPrincipal;

import jakarta.annotation.Priority;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

	private static Logger logger = LogManager.getLogger(AuthenticationFilter.class);
	private static final Cache<Method, Boolean> CACHE = CacheManager.getInstance().getCache("isEndpointAuthGated", Method.class, Boolean.class);

	@Context
	private ResourceInfo resourceInfo;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		if (HttpMethod.OPTIONS.equals(requestContext.getMethod())) { // Allow CORS pre-flight requests
			return;
		}
		
		if (isPublicEndpoint(requestContext)) {
			return;
		}

		String auth = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

		if (auth == null) {
			logger.warn("An unauthenticated user attempted to call an auth-gated endpoint. Public endpoints must declare the @Public annotation.");
			throw new WebApplicationException(Response.status(Status.UNAUTHORIZED).build());
		}

		auth = auth.trim();

		if (!auth.startsWith("Bearer ")) {
			logger.warn("An user attempted to call an auth-gated endpoint with a malformed authorization header.");
			throw new WebApplicationException(Response.status(Status.UNAUTHORIZED).build());
		}

		String token = auth.substring(7).trim();
		UserPrincipal user;

		try {
			Session session = Session.decode(token);
			user = session.getUser();
		} catch (InvalidTokenException e) {
			logger.warn("An user attempted to call an auth-gated endpoint with an invalid or expired token.", e);
			throw new WebApplicationException(Response.status(Status.UNAUTHORIZED).build());
		}

		requestContext.setSecurityContext(createSecurityContext(requestContext, user));
	}

	private boolean isPublicEndpoint(ContainerRequestContext requestContext) {
		Class<?> clazz = resourceInfo.getResourceClass();
		Method method = resourceInfo.getResourceMethod();

		return CACHE.computeIfAbsent(method, (m) -> clazz.isAnnotationPresent(Public.class) || method.isAnnotationPresent(Public.class));
	}

	private SecurityContext createSecurityContext(ContainerRequestContext requestContext, UserPrincipal principal) {
		SecurityContext current = requestContext.getSecurityContext();

		return new SecurityContext() {

			@Override
			public boolean isUserInRole(String role) {
				return principal.getRole().equals(role);
			}

			@Override
			public boolean isSecure() {
				return current.isSecure();
			}

			@Override
			public Principal getUserPrincipal() {
				return principal;
			}

			@Override
			public String getAuthenticationScheme() {
				return current.getAuthenticationScheme();
			}
		};
	}

}
