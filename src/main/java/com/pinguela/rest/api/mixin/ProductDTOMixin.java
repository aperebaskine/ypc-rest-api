package com.pinguela.rest.api.mixin;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pinguela.yourpc.model.dto.AttributeDTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Product")
public abstract class ProductDTOMixin {

	@JsonProperty
	private Long id;

	@JsonProperty
	private Short categoryId;

	@JsonIgnore
	private String category;

	@JsonProperty
	private String name;

	@JsonProperty
	private String description;

	@JsonProperty
	private Date launchDate;

	@JsonIgnore
	private Date discontinuationDate;

	@JsonProperty
	private Integer stock;

	@JsonIgnore
	private Double purchasePrice;

	@JsonProperty
	private Double salePrice;

	@JsonProperty
	private Long replacementId;

	@JsonIgnore
	private String replacementName;

	@JsonIgnore
	private Map<String, AttributeDTO<?>> attributes;

}
