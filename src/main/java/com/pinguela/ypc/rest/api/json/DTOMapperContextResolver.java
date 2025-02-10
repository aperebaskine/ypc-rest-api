package com.pinguela.ypc.rest.api.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinguela.rest.api.mixin.AttributeDTOMixin;
import com.pinguela.rest.api.mixin.ProductDTOMixin;
import com.pinguela.yourpc.model.dto.AttributeDTO;
import com.pinguela.yourpc.model.dto.LocalizedProductDTO;

import jakarta.ws.rs.ext.ContextResolver;

public class DTOMapperContextResolver 
implements ContextResolver<ObjectMapper> {

	private final ObjectMapper mapper;

	public DTOMapperContextResolver() {
		mapper = new ObjectMapper();

		mapper.addMixIn(AttributeDTO.class, AttributeDTOMixin.class);
		mapper.addMixIn(LocalizedProductDTO.class, ProductDTOMixin.class);
	}

	@Override
	public ObjectMapper getContext(Class<?> type) {
		return mapper;
	}

}
