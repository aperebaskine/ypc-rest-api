package com.pinguela.ypc.rest.api.model;

import java.security.Principal;

import com.pinguela.yourpc.model.User;

public class UserPrincipal implements Principal {
	
	private Integer id;
	private String name;
	private String role;
	private String email;
	
	public UserPrincipal(User user) {
		this.id = user.getId();
		this.name = user.getFirstName();
		this.role = user.getRoleId();
		this.email = user.getEmail();
	}
	
	public UserPrincipal(Integer id, String name, String role, String email) {
		super();
		this.id = id;
		this.name = name;
		this.role = role;
		this.email = email;
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
	
	public String getEmail() {
		return email;
	}
	
}
