package com.pinguela.ypc.rest.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SessionToken {
	
	@JsonProperty
	private String token;
	
	public SessionToken(String token) {
		this.token = token;
	}
	
	public String getToken() {
		return token;
	}

}
