package com.pinguela.ypc.rest.api.util;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.pinguela.yourpc.config.ConfigManager;

public class PathUtils {
	
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
	
	public static String createPath(String path) {
		return API_BASE_URL.resolve(path).getPath();
	}

}
