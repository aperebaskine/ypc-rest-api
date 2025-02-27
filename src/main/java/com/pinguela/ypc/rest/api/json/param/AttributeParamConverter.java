package com.pinguela.ypc.rest.api.json.param;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.pinguela.yourpc.model.dto.AttributeDTO;
import com.pinguela.ypc.rest.api.json.serialize.LightAttributeDeserializer;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.ParamConverter;

public class AttributeParamConverter implements ParamConverter<AttributeDTO<?>> {
	
	private static final AttributeParamConverter INSTANCE = new AttributeParamConverter();

	private static final Logger logger = LogManager.getLogger(AttributeParamConverter.class);

	private ObjectMapper mapper;
	
	private AttributeParamConverter() {
		mapper = new ObjectMapper();

		SimpleModule module = new SimpleModule();
		module.addDeserializer(AttributeDTO.class, new LightAttributeDeserializer());

		mapper.registerModule(module);
	}
	
	public static AttributeParamConverter getInstance() {
		return INSTANCE;
	}

	@Override
	public AttributeDTO<?> fromString(String value) {
		
		try {
			return mapper.readerFor(AttributeDTO.class).readValue(value);
		} catch (JsonProcessingException e) {
			logger.error(e);
			throw new WebApplicationException(e);
		}
	}

	@Override
	public String toString(AttributeDTO<?> value) {
		throw new UnsupportedOperationException("This method should never be called!");
	}
}
