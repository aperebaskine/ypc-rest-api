package com.pinguela.ypc.rest.api.util;

import com.pinguela.ypc.rest.api.model.UserPrincipal;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.SecurityContext;

public class AuthUtils {
	
	public static Integer getUserId(ContainerRequestContext context) {
		return ((UserPrincipal) (context.getSecurityContext().getUserPrincipal())).getId();
	}
	
	public static Integer getUserId(SecurityContext context) {
		return ((UserPrincipal) (context.getUserPrincipal())).getId();
	}

}
