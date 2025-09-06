package com.pinguela.ypc.rest.api.model;

import java.security.Principal;

public class UserPrincipal implements Principal {
	
	private Integer id;
	private String name;
	private String role;
	
	public UserPrincipal(Integer id, String name, String role) {
		super();
		this.id = id;
		this.name = name;
		this.role = role;
	}
	
	public Integer getId() {
		return id;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	public String getRole() {
		return role;
	}
	
}
