package com.pinguela.ypc.rest.api.util;

import java.net.URI;

import jakarta.ws.rs.container.ContainerRequestContext;

public class PathUtils {
	
	public static String createPath(URI baseUri, String path) {
		return baseUri.resolve(path).getPath();
	}
	
	public static String createPath(ContainerRequestContext context, String path) {
		URI baseUri = context.getUriInfo().getBaseUri();
		return createPath(baseUri, path);
	}

}
