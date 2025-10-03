package com.pinguela.ypc.rest.api.cookies;

import com.pinguela.ypc.rest.api.constants.Paths;

import jakarta.ws.rs.core.NewCookie.SameSite;

/**
 * Configuration for short-lived, single-use cookies used during the authorization code flow.
 */
public enum OAuthFlowCookie implements CookieConfiguration {
	ORIGIN("__Secure-oauth_request_origin"),
	PROVIDER("__Secure-oauth_provider"),
	CODE_VERIFIER("__Secure-oauth_code_verifier"),
	NONCE("__Secure-oauth_nonce");
	
	private final String name;
	
	OAuthFlowCookie(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public String getPath() {
		return Paths.OAUTH_CALLBACK;
	}
	
	public int getMaxAge() {
		return 300;
	}
	
	public boolean isHttpOnly() {
		return true;
	}
	
	public boolean isSecure() {
		return true;
	}
	
	public SameSite getSameSite() {
		return SameSite.LAX;
	}
	
}
