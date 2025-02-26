package com.pinguela.ypc.rest.api.json.param;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import com.pinguela.yourpc.model.dto.AttributeDTO;

import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import jakarta.ws.rs.ext.Provider;

@Provider
public class AttributeParamConverterProvider implements ParamConverterProvider {

	@SuppressWarnings("unchecked")
	@Override
	public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {

		for (Annotation annotation : annotations) {
			if (annotation instanceof QueryParam
					&& AttributeDTO.class.isAssignableFrom(rawType)) {
				return (ParamConverter<T>) AttributeParamConverter.getInstance();
			}
		}

		return null;
	}


}
