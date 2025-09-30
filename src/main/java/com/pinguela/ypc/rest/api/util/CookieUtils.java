package com.pinguela.ypc.rest.api.util;

import com.pinguela.ypc.rest.api.cookies.CookieConfiguration;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.NewCookie;

public class CookieUtils {
	
	public static NewCookie newCookie(ContainerRequestContext context, CookieConfiguration config, String value) {
		String path = PathUtils.createPath(context, config.getPath());
		return new NewCookie.Builder(config.getName())
				.value(value)
				.domain(config.getDomain())
				.path(path)
				.maxAge(config.getMaxAge())
				.httpOnly(config.isHttpOnly())
				.secure(config.isSecure())
				.sameSite(config.getSameSite())
				.build();
	}

}
