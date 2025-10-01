package com.pinguela.ypc.rest.api.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pinguela.yourpc.model.Address;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Schema(name = "Customer")
public class CustomerDTOMixin {
	
	@JsonProperty
	@Schema(nullable = false, requiredMode = RequiredMode.REQUIRED)
	private Integer id;
	
	@JsonIgnore
	private Integer roleId;
	
	@JsonProperty
	@Schema(nullable = false, requiredMode = RequiredMode.REQUIRED, type = "integer", format = "int64")
	private Date creationDate;
	
	@JsonProperty
	@Schema(nullable = false, requiredMode = RequiredMode.REQUIRED)
	private String firstName;
	
	@JsonProperty
	@Schema(nullable = false, requiredMode = RequiredMode.REQUIRED)
	private String lastName1;
	
	@JsonProperty
	@Schema(nullable = true)
	private String lastName2;
	
	@JsonProperty
	@Schema(nullable = false, accessMode = AccessMode.READ_ONLY)
	private String getFullName() { return null; }
	
	@JsonProperty
	@Schema(nullable = false, requiredMode = RequiredMode.REQUIRED)
	private String documentTypeId;
	
	@JsonIgnore
	private String documentType;
	
	@JsonProperty
	@Schema(nullable = false, requiredMode = RequiredMode.REQUIRED)
	private String documentNumber;
	
	@JsonProperty
	@Schema(nullable = false, requiredMode = RequiredMode.REQUIRED)
	private String phoneNumber;
	
	@JsonProperty
	@Schema(nullable = false, requiredMode = RequiredMode.REQUIRED)
	private String email;
	
	@JsonIgnore
	private String unencryptedPassword;
	
	@JsonIgnore
	private String encryptedPassword;
	
	@JsonIgnore
	private List<Address> addresses;

}
