package com.pinguela.ypc.rest.api.mixin;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pinguela.yourpc.model.dto.AttributeValueDTO;
import com.pinguela.ypc.rest.api.json.serialize.AttributeDataTypeSerializer;
import com.pinguela.ypc.rest.api.json.serialize.AttributeHandlingModeSerializer;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Attribute")
public abstract class AttributeDTOMixin {

	@JsonProperty
	private Integer id;

	@JsonProperty
	private String name;
	
	@JsonProperty
	@ArraySchema(schema = @Schema(implementation = AttributeValueDTO.class))
	private List<AttributeValueDTO<?>> values;
	
	@JsonGetter("dataType")
	@JsonSerialize(using = AttributeDataTypeSerializer.class)
	abstract String getDataTypeIdentifier();
	
	@Schema(type = "string")
	@JsonGetter("handlingMode")
	@JsonSerialize(using = AttributeHandlingModeSerializer.class)
	abstract Integer getValueHandlingMode();

	@JsonIgnore
	abstract List<AttributeValueDTO<?>> getValuesByHandlingMode();

	@JsonIgnore
	abstract Class<?> getTypeParameterClass();
	
	private AttributeDTOMixin() {}

}
