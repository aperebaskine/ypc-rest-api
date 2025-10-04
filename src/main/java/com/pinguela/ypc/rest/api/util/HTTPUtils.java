package com.pinguela.ypc.rest.api.util;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.pinguela.yourpc.config.ConfigManager;
import com.pinguela.ypc.rest.api.cookies.CookieConfiguration;

import jakarta.ws.rs.core.NewCookie;

public class HTTPUtils {
	
	private static final URI API_BASE_URL;
	private static final Set<String> ALLOWED_ORIGINS;
	
	static {
		String[] allowedOrigins = ConfigManager.getParameters("http.origin.allowlist");
		ALLOWED_ORIGINS = new HashSet<String>(Arrays.asList(allowedOrigins));
		
		String apiBasePath = ConfigManager.getParameter("http.api_base_path");
		API_BASE_URL = URI.create(apiBasePath);
	}
	
	public static boolean isOriginAllowed(String origin) {
		return ALLOWED_ORIGINS.contains(origin);
	}
	
	public static String createUrl(String path) {
		return API_BASE_URL.resolve(path).toString();
	}
	
	public static String createPath(String path) {
		return API_BASE_URL.resolve(path).getPath();
	}

	public static NewCookie newCookie(CookieConfiguration config, String value) {
		String path = createPath(config.getPath());
		return new NewCookie.Builder(config.getName())
				.value(value)
				.path(path)
				.domain(config.getDomain())
				.maxAge(config.getMaxAge())
				.httpOnly(config.isHttpOnly())
				.secure(config.isSecure())
				.sameSite(config.getSameSite())
				.build();
	}
	
	public static NewCookie newCookieWithExpiry(CookieConfiguration config, String value) {
		String path = createPath(config.getPath());
		return new NewCookie.Builder(config.getName())
				.value(value)
				.path(path)
				.domain(config.getDomain())
				.expiry(config.getExpiry())
				.httpOnly(config.isHttpOnly())
				.secure(config.isSecure())
				.sameSite(config.getSameSite())
				.build();
	}

	public static NewCookie expiredCookie(CookieConfiguration config) {
		String path = createPath(config.getPath());
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
