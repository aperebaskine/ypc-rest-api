package com.pinguela.ypc.rest.api.json.serialize;

import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.pinguela.DataException;
import com.pinguela.ServiceException;
import com.pinguela.yourpc.model.dto.AttributeDTO;
import com.pinguela.yourpc.service.AttributeService;
import com.pinguela.yourpc.service.impl.AttributeServiceImpl;
import com.pinguela.ypc.rest.api.constants.AttributeJsonMappings;

@SuppressWarnings("serial")
public class AttributeDeserializer extends StdDeserializer<AttributeDTO<?>> {
	
	private static Logger logger = LogManager.getLogger(AttributeDeserializer.class);
	
	// TODO: Cache values
	private AttributeService attributeService;

	public AttributeDeserializer() {
		super(AttributeDTO.class);
		this.attributeService = new AttributeServiceImpl();
	}

	@Override
	public AttributeDTO<?> deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JacksonException {

		JsonNode root = (JsonNode) p.readValueAsTree();
		
		Integer id = root.get("id").asInt();
		
		JsonNode dataTypeNode = root.get("dataType");
		String dataType;
		
		try {
			dataType = dataTypeNode == null ? 
					attributeService.findById(id, Locale.forLanguageTag("en-GB"), false, null).getDataTypeIdentifier() :
					AttributeJsonMappings.getDataType(dataTypeNode.asText());
		} catch (ServiceException | DataException e) {
			String errorMsg = String.format("Exception thrown while fetching attribute data: %s", e.getMessage());
			logger.error(errorMsg, e);
			throw new JsonMappingException(p, errorMsg);
		}

		AttributeDTO<?> dto = AttributeDTO.getInstance(dataType);
		dto.setId(root.get("id").asInt());

		Iterator<JsonNode> values = root.get("values").elements();
		while (values.hasNext()) {
			JsonNode value = values.next();
			dto.addValue(
					value.get("id").asLong(),
					value.get("value").traverse(p.getCodec())
					.readValueAs(dto.getTypeParameterClass())
					);
		}

		return dto;
	}

}
