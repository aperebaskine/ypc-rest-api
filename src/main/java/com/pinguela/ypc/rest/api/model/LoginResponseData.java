package com.pinguela.ypc.rest.api.model;

import jakarta.ws.rs.core.NewCookie;

public class LoginResponseData {
	
	private String token;
	private NewCookie cookie;
	
	public LoginResponseData(String token, NewCookie cookie) {
		super();
		this.token = token;
		this.cookie = cookie;
	}
	
	public String getToken() {
		return token;
	}
	
	public NewCookie getCookie() {
		return cookie;
	}

}
