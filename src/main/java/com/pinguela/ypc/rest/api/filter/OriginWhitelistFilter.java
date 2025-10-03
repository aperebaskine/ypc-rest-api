package com.pinguela.ypc.rest.api.filter;

import java.io.IOException;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.http.HttpHeaders;

import com.pinguela.ypc.rest.api.util.PathUtils;

@Provider
@PreMatching
@Priority(Priorities.AUTHORIZATION)
public class OriginWhitelistFilter implements ContainerRequestFilter {

	private static Logger logger = LogManager.getLogger(OriginWhitelistFilter.class);
	
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		
		String origin = requestContext.getHeaderString(HttpHeaders.ORIGIN);
		
		if (origin == null) {
			return;
		}
		
		if (!PathUtils.isOriginAllowed(origin)) {
			logger.info("Received request with non-allowed origin {}.", origin);
			throw new WebApplicationException(Status.BAD_REQUEST);
		}
	}

}
