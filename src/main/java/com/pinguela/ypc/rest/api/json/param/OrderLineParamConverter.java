package com.pinguela.ypc.rest.api.json.param;

import org.apache.commons.validator.GenericValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinguela.yourpc.model.OrderLine;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.ParamConverter;

public class OrderLineParamConverter implements ParamConverter<OrderLine> {
	
	private static Logger logger = LogManager.getLogger(OrderLineParamConverter.class);
	
	private ObjectMapper mapper;
	private JavaType type;
	
	public OrderLineParamConverter() {
		mapper = new ObjectMapper();
		type = mapper.constructType(new TypeReference<OrderLine>() {});
	}

	@Override
	public OrderLine fromString(String value) {
		
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
	public String toString(OrderLine value) {
		try {
			return mapper.writerFor(type).writeValueAsString(value);
		} catch (JsonProcessingException e) {
			logger.error(e);
			throw new WebApplicationException(e);
		}
	}

}
