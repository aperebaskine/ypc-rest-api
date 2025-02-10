package com.pinguela.ypc.rest.api.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.pinguela.ypc.rest.api.constants.AttributeJsonTypes;

@SuppressWarnings("serial")
public class AttributeDataTypeSerializer extends StdSerializer<String> {

	protected AttributeDataTypeSerializer() {
		super(String.class);
	}

	@Override
	public void serialize(String value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeString(AttributeJsonTypes.getJsonType(value));
	}

}
