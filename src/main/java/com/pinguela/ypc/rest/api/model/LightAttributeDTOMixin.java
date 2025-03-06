package com.pinguela.ypc.rest.api.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pinguela.yourpc.model.dto.AttributeValueDTO;
import com.pinguela.ypc.rest.api.schema.AttributeValueModelConverter;

import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Schema(name = "LightAttribute")
public class LightAttributeDTOMixin {

	@JsonProperty
	@Schema(nullable = false, requiredMode = RequiredMode.REQUIRED)
	private Integer id;

	@JsonProperty
	@ArraySchema(
			schema = @Schema(
					implementation = AttributeValueDTO.class, 
					requiredMode = RequiredMode.REQUIRED,
					extensions = @Extension(
							name = AttributeValueModelConverter.X_SCHEMA_REPRESENTATION, 
							properties = @ExtensionProperty(
									name = AttributeValueModelConverter.REPRESENTATION_FORMAT, 
									value = AttributeValueModelConverter.COMPACT_FORMAT
									)
							)
					)
			)
	private List<AttributeValueDTO<?>> values;
}
