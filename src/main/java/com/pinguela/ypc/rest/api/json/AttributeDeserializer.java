package com.pinguela.ypc.rest.api.json;

import java.io.IOException;
import java.util.Iterator;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.pinguela.yourpc.model.dto.AttributeDTO;

@SuppressWarnings("serial")
public class AttributeDeserializer extends StdDeserializer<AttributeDTO<?>> {

	public AttributeDeserializer() {
		super(AttributeDTO.class);
	}

	@Override
	public AttributeDTO<?> deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JacksonException {

		JsonNode root = (JsonNode) p.readValueAsTree();
		String dataType = root.get("dataType").asText();

		AttributeDTO<?> dto = AttributeDTO.getInstance(dataType);
		dto.setId(root.get("id").asInt());

		Iterator<JsonNode> values = root.get("values").elements();
		while (values.hasNext()) {
			JsonNode value = values.next();
			dto.addValue(
					value.get("id").asLong(),
					value.get("").traverse(p.getCodec())
					.readValueAs(dto.getTypeParameterClass())
					);
		}

		return dto;
	}

}
