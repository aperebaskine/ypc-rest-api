package com.pinguela.ypc.rest.api.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinguela.yourpc.model.dto.AttributeDTO;
import com.pinguela.yourpc.model.dto.LocalizedProductDTO;
import com.pinguela.ypc.rest.api.mixin.AttributeDTOMixin;
import com.pinguela.ypc.rest.api.mixin.ProductDTOMixin;

import jakarta.ws.rs.ext.ContextResolver;

public class DTOMixinMapperContextResolver 
implements ContextResolver<ObjectMapper> {

	private final ObjectMapper mapper;

	public DTOMixinMapperContextResolver() {
		mapper = new ObjectMapper();

		mapper.addMixIn(AttributeDTO.class, AttributeDTOMixin.class);
		mapper.addMixIn(LocalizedProductDTO.class, ProductDTOMixin.class);
	}

	@Override
	public ObjectMapper getContext(Class<?> type) {
		if (mapper.findMixInClassFor(type) != null) {
			return mapper;
		}
		return null;
	}

}
