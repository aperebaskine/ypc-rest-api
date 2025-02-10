package com.pinguela.ypc.rest.api.mixin;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pinguela.yourpc.model.dto.AttributeDTO;
import com.pinguela.ypc.rest.api.json.serialize.AttributeMapDeserializer;
import com.pinguela.ypc.rest.api.json.serialize.MapToValueArraySerializer;

import io.swagger.v3.oas.annotations.media.ArraySchema;
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

	@JsonProperty
	@JsonSerialize(using = MapToValueArraySerializer.class)
	@JsonDeserialize(using = AttributeMapDeserializer.class)
	@ArraySchema(schema = @Schema(implementation = AttributeDTOMixin.class))
	private Map<String, AttributeDTO<?>> attributes;
	
	private ProductDTOMixin() {}

}
