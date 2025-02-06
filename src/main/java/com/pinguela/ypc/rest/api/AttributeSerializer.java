package com.pinguela.ypc.rest.api;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.pinguela.yourpc.model.dto.AttributeDTO;
import com.pinguela.yourpc.model.dto.AttributeValueDTO;

@SuppressWarnings("serial")
public class AttributeSerializer extends StdSerializer<AttributeDTO<?>> {

	@SuppressWarnings("unchecked")
	protected AttributeSerializer() {
		super((Class<AttributeDTO<?>>) ((Class<?>) AttributeDTO.class));
	}

	@Override
	public void serialize(AttributeDTO<?> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeStartObject();
		gen.writeNumberField("id", value.getId());
		gen.writeStringField("name", value.getName());
		gen.writeStringField("dataType", value.getDataTypeIdentifier());
		gen.writeArrayFieldStart("values");
		
		for (AttributeValueDTO<?> av : value.getValues()) {
			gen.writeStartObject();
			gen.writeObjectField("id", av.getId());
			gen.writeObjectField("value", av.getValue());
			gen.writeEndObject();
		}
		
		gen.writeEndArray();
		gen.writeEndObject();
	}
}
