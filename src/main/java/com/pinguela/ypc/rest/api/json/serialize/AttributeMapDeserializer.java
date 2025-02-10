package com.pinguela.ypc.rest.api.json.serialize;

import java.util.TreeMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.pinguela.yourpc.model.dto.AttributeDTO;

@SuppressWarnings("serial")
public class AttributeMapDeserializer extends ValueArrayToMapDeserializer<String, AttributeDTO<?>> {

	public AttributeMapDeserializer() {
		super(() -> new TreeMap<>(), 
				new TypeReference<String>() {}, 
				new TypeReference<AttributeDTO<?>>() {}, 
				dto -> dto.getName());
	}
	
}
