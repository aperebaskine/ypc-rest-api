package com.pinguela.ypc.rest.api.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Schema(name = "Address")
public abstract class AddressDTOMixin {

	@JsonProperty
	@Schema(nullable = false, accessMode = AccessMode.READ_ONLY)
	private Integer id;

	@JsonProperty
	@Schema(nullable = false, requiredMode = RequiredMode.REQUIRED)
	private String name;

	@JsonProperty
	@Schema(nullable = true, requiredMode = RequiredMode.NOT_REQUIRED)
	private Integer customerId;

	@JsonProperty
	@Schema(nullable = true, requiredMode = RequiredMode.NOT_REQUIRED)
	private Integer employeeId;

	@JsonProperty
	@Schema(nullable = false, requiredMode = RequiredMode.REQUIRED)
	private String streetName;

	@JsonProperty
	@Schema(nullable = true, requiredMode = RequiredMode.NOT_REQUIRED)
	private Short streetNumber;

	@JsonProperty
	@Schema(nullable = true, requiredMode = RequiredMode.NOT_REQUIRED)
	private Short floor;

	@JsonProperty
	@Schema(nullable = true, requiredMode = RequiredMode.NOT_REQUIRED)
	private String door;

	@JsonProperty
	@Schema(nullable = false, requiredMode = RequiredMode.REQUIRED)
	private String zipCode;

	@JsonProperty
	@Schema(nullable = false, requiredMode = RequiredMode.REQUIRED)
	private Integer cityId;

	@JsonProperty
	@Schema(nullable = false, accessMode = AccessMode.READ_ONLY)
	private String city;

	@JsonProperty
	@Schema(nullable = false, accessMode = AccessMode.READ_ONLY)
	private Integer provinceId;

	@JsonProperty
	@Schema(nullable = false, accessMode = AccessMode.READ_ONLY)
	private String province;

	@JsonProperty
	@Schema(nullable = false, accessMode = AccessMode.READ_ONLY)
	private String countryId;

	@JsonProperty
	@Schema(nullable = false, accessMode = AccessMode.READ_ONLY)
	private String country;

	@JsonProperty("isDefault")
	@Schema(nullable = false, requiredMode = RequiredMode.NOT_REQUIRED)
	private Boolean isDefault = false;

	@JsonProperty("isBilling")
	@Schema(nullable = false, requiredMode = RequiredMode.NOT_REQUIRED)
	private Boolean isBilling = false;

	@JsonProperty
	@Schema(nullable = true, type = "integer", format = "int64", accessMode = AccessMode.READ_ONLY)
	private Date creationDate;
	
	@JsonIgnore
	private Boolean isDefault() { return false; }
	
	@JsonIgnore
	private Boolean isBilling() { return false; }

}
