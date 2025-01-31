package com.pinguela.ypc.rest.api;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinguela.yourpc.model.dto.AttributeDTO;

public class AttributeReader implements MessageBodyReader<List<AttributeDTO<?>>> {

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return MediaType.APPLICATION_JSON.equals(mediaType.getType());
	}

	@Override
	public List<AttributeDTO<?>> readFrom(Class<List<AttributeDTO<?>>> type, Type genericType, Annotation[] annotations,
			MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {
		
		ObjectMapper mapper = new ObjectMapper();
		
		mapper.readValue(entityStream, mapper.getTypeFactory().constructCollectionType(List.class, AttributeDTO.class));
		
		// TODO Auto-generated method stub
		return null;
	}

}
