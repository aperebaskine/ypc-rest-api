package com.pinguela.ypc.rest.api.json.param;

import java.util.List;

import org.apache.commons.validator.GenericValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.pinguela.yourpc.model.dto.AttributeDTO;
import com.pinguela.ypc.rest.api.json.serialize.LightAttributeDeserializer;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.ParamConverter;

public class AttributeListParamConverter implements ParamConverter<List<AttributeDTO<?>>> {
	
	private static final AttributeListParamConverter INSTANCE = new AttributeListParamConverter();

	private static final Logger logger = LogManager.getLogger(AttributeListParamConverter.class);

	private ObjectMapper mapper;
	private JavaType type;
	
	private AttributeListParamConverter() {
		mapper = new ObjectMapper();
		type = mapper.constructType(new TypeReference<List<AttributeDTO<?>>>() {});

		SimpleModule module = new SimpleModule();
		module.addDeserializer(AttributeDTO.class, new LightAttributeDeserializer());

		mapper.registerModule(module);
	}
	
	public static AttributeListParamConverter getInstance() {
		return INSTANCE;
	}

	@Override
	public List<AttributeDTO<?>> fromString(String value) {
		
		if (GenericValidator.isBlankOrNull(value)) {
			return null;
		}
		
		try {
			return mapper.readerFor(type).readValue(value);
		} catch (JsonProcessingException e) {
			logger.error(e);
			throw new WebApplicationException(e);
		}
	}

	@Override
	public String toString(List<AttributeDTO<?>> value) {
		throw new UnsupportedOperationException("This method should never be called!");
	}
}
