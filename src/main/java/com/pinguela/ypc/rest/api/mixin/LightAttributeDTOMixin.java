package com.pinguela.ypc.rest.api.mixin;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pinguela.yourpc.model.dto.AttributeValueDTO;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "LightAttribute")
public class LightAttributeDTOMixin {
	
	@JsonProperty
	private Integer id;
	
	@JsonProperty
	@ArraySchema(schema = @Schema(implementation = AttributeValueDTO.class))
	private List<AttributeValueDTO<?>> values;
}
