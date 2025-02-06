package com.pinguela.ypc.rest.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.pinguela.yourpc.model.dto.AttributeDTO;

import jakarta.ws.rs.ext.ContextResolver;

public class AttributeMapperContextResolver 
implements ContextResolver<ObjectMapper> {

	private final ObjectMapper mapper;
	
	public AttributeMapperContextResolver() {
		mapper = new ObjectMapper();
		
		SimpleModule module = new SimpleModule();
		module.addSerializer(new AttributeSerializer());
		module.addDeserializer(AttributeDTO.class, new AttributeDeserializer());
		
		mapper.registerModule(module);
	}
	
	@Override
	public ObjectMapper getContext(Class<?> type) {
		return mapper;
	}

}
