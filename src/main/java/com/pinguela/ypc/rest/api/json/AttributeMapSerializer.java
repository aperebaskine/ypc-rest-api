package com.pinguela.ypc.rest.api.json;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.pinguela.yourpc.model.dto.AttributeDTO;

@SuppressWarnings("serial")
public class AttributeMapSerializer extends StdSerializer<Map<String, AttributeDTO<?>>> {

	public AttributeMapSerializer() {
		super(constructJavaType());
	}

	@Override
	public void serialize(Map<String, AttributeDTO<?>> map, JsonGenerator gen,
			SerializerProvider provider) throws IOException {

		gen.writeStartArray();

		for (AttributeDTO<?> value : map.values()) {
			gen.writeObject(value);
		}

		gen.writeEndArray();
	}

	private static JavaType constructJavaType() {
		return new ObjectMapper()
				.getTypeFactory()
				.constructMapType(Map.class, String.class, AttributeDTO.class);
	}

}
