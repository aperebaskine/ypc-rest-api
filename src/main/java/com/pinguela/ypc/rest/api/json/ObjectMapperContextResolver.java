package com.pinguela.ypc.rest.api.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.pinguela.yourpc.model.dto.AttributeDTO;
import com.pinguela.yourpc.model.dto.LocalizedProductDTO;
import com.pinguela.ypc.rest.api.model.AttributeDTOMixin;
import com.pinguela.ypc.rest.api.model.ProductDTOMixin;

import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ObjectMapperContextResolver 
implements ContextResolver<ObjectMapper> {

	private final ObjectMapper mapper;

	public ObjectMapperContextResolver() {
		
		mapper = new ObjectMapper();
		
		SimpleModule module = new SimpleModule();
		module.setMixInAnnotation(AttributeDTO.class, AttributeDTOMixin.class);
		module.setMixInAnnotation(LocalizedProductDTO.class, ProductDTOMixin.class);
		
		mapper.registerModule(module);
	}

	@Override
	public ObjectMapper getContext(Class<?> type) {
		return mapper;
	}

}
