package com.pinguela.rest.api.mixin;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pinguela.yourpc.model.dto.AttributeValueDTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Attribute")
public abstract class AttributeDTOMixin {

	@JsonProperty
	private Integer id;

	@JsonProperty
	private String name;

	@JsonProperty("dataType")
	abstract String getDataTypeIdentifier();

	@JsonProperty
	private List<AttributeValueDTO<?>> values; 

	@JsonIgnore
	abstract List<AttributeValueDTO<?>> getValuesByHandlingMode();

	@JsonIgnore
	abstract Integer getValueHandlingMode();

	@JsonIgnore
	abstract Class<?> getTypeParameterClass();

}
