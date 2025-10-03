package com.pinguela.ypc.rest.api.util;

import com.pinguela.ypc.rest.api.cookies.CookieConfiguration;

import jakarta.ws.rs.core.NewCookie;

public class CookieUtils {
	
	public static NewCookie newCookie(CookieConfiguration config, String value) {
		String path = PathUtils.createPath(config.getPath());
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
	
	public static NewCookie expiredCookie(CookieConfiguration config) {
		String path = PathUtils.createPath(config.getPath());
		return new NewCookie.Builder(config.getName())
				.domain(config.getDomain())
				.path(path)
				.maxAge(0)
				.httpOnly(config.isHttpOnly())
				.secure(config.isSecure())
				.sameSite(config.getSameSite())
				.build();
	}

}
