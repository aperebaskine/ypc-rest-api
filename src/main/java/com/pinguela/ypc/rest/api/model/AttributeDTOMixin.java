package com.pinguela.ypc.rest.api.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pinguela.yourpc.model.dto.AttributeValueDTO;
import com.pinguela.ypc.rest.api.json.serialize.AttributeDataTypeSerializer;
import com.pinguela.ypc.rest.api.json.serialize.AttributeHandlingModeSerializer;
import com.pinguela.ypc.rest.api.schema.AttributeValueModelConverter;

import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Schema(name = "Attribute")
public abstract class AttributeDTOMixin {

	@JsonProperty
	@Schema(nullable = false, requiredMode = RequiredMode.REQUIRED)
	private Integer id;

	@JsonProperty
	@Schema(nullable = false, requiredMode = RequiredMode.REQUIRED)
	private String name;

	@JsonProperty
	@ArraySchema(
			schema = @Schema(
					implementation = AttributeValueDTO.class, 
					requiredMode = RequiredMode.REQUIRED,
					extensions = @Extension(
							name = AttributeValueModelConverter.X_SCHEMA_REPRESENTATION, 
							properties = @ExtensionProperty(
									name = AttributeValueModelConverter.REPRESENTATION_FORMAT, 
									value = AttributeValueModelConverter.COMPLETE_FORMAT
									)
							)
					)
			)
	private List<AttributeValueDTO<?>> values;

	@JsonGetter("dataType")
	@JsonSerialize(using = AttributeDataTypeSerializer.class)
	@Schema(nullable = false, requiredMode = RequiredMode.REQUIRED)
	abstract String getDataTypeIdentifier();

	@Schema(type = "string", nullable = false, requiredMode = RequiredMode.REQUIRED)
	@JsonGetter("handlingMode")
	@JsonSerialize(using = AttributeHandlingModeSerializer.class)
	abstract Integer getValueHandlingMode();

	@JsonIgnore
	abstract List<AttributeValueDTO<?>> getValuesByHandlingMode();

	@JsonIgnore
	abstract Class<?> getTypeParameterClass();

	private AttributeDTOMixin() {}

}
