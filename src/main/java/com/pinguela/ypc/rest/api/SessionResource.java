package com.pinguela.ypc.rest.api;

import java.time.Duration;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pinguela.ypc.rest.api.annotations.Public;
import com.pinguela.ypc.rest.api.constants.Paths;
import com.pinguela.ypc.rest.api.cookies.SessionCookieConfig;
import com.pinguela.ypc.rest.api.exception.ValidationException;
import com.pinguela.ypc.rest.api.login.OAuthManager;
import com.pinguela.ypc.rest.api.model.Session;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/")
@Tag(name = "session")
public class SessionResource {

	private static Logger logger = LogManager.getLogger(SessionResource.class);
	private static OAuthManager oauthManager = OAuthManager.getInstance();
	
	@GET
	@Public
	@Path(Paths.SESSION)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(com.pinguela.ypc.rest.api.constants.MediaType.APPLICATION_JWT)
	@Operation(
			method = "GET",
			operationId = "refreshSession",
			description = "Refreshes the user session from cookies, returning a short-lived JWT to be used as Bearer authorization header token.", 
			responses = {
					@ApiResponse(
							responseCode = "200", 
							description = "Successfully refreshed session",
							content = @Content(
									mediaType = com.pinguela.ypc.rest.api.constants.MediaType.APPLICATION_JWT,
									schema = @Schema(
											type = "string",
											format = "byte"
											)
									)
							),
					@ApiResponse(
							responseCode = "401",
							description = "Caller is unauthenticated"
							)
			})
	public Response refreshSession(
			@Context ContainerRequestContext requestContext
			) {
		Map<String, Cookie> cookies = requestContext.getCookies();
		Cookie sessionCookie = cookies.get(SessionCookieConfig.getInstance().getName());

		if (sessionCookie == null) {
			return Response.status(Status.UNAUTHORIZED).build();
		}

		Session session;

		try {
			session = Session.decode(sessionCookie.getValue());

			String newSession;

			switch (session.getType()) {
			case CREDENTIALS:
				newSession = session.encode(Duration.ofMinutes(10));
				break;
			case OAUTH:
				newSession = oauthManager.getSession(cookies);
				break;
			default:
				throw new IllegalStateException();
			}

			return Response.ok(newSession)
					.build();
		} catch (ValidationException e) {
			logger.warn(e);
			return Response.status(Status.UNAUTHORIZED).build();
		}
	}
	
}
