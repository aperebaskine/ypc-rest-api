package com.pinguela.ypc.rest.api.cookies;

import java.time.Instant;
import java.util.Date;

import jakarta.ws.rs.core.NewCookie.SameSite;

public interface CookieConfiguration {
	
	String getName();
	
	default String getDomain() {
		return null;
	}
	
	default String getPath() {
		return null;
	}
	
	int getMaxAge();
	
	default Date getExpiry() {
		int maxAge = this.getMaxAge();
		return Date.from(Instant.now().plusSeconds(maxAge));
	}
	
	boolean isHttpOnly();
	
	boolean isSecure();
	
	SameSite getSameSite();

}
