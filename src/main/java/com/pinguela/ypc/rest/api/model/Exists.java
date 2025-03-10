package com.pinguela.ypc.rest.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Exists {

	@JsonProperty
	private Boolean email;
	
	@JsonProperty
	private Boolean phoneNumber;

	public Exists(Boolean email, Boolean phoneNumber) {
		this.email = email;
		this.phoneNumber = phoneNumber;
	}

	public boolean emailExists() {
		return email;
	}

	public void setEmail(Boolean email) {
		this.email = email;
	}

	public boolean phoneNumberExists() {
		return phoneNumber;
	}

	public void setPhoneNumber(Boolean phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
}
