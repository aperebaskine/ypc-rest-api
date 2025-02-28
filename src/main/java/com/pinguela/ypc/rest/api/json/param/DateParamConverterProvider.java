package com.pinguela.ypc.rest.api.json.param;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Date;

import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import jakarta.ws.rs.ext.Provider;

@Provider
public class DateParamConverterProvider implements ParamConverterProvider {
	
	private static final DateParamConverter CONVERTER = new DateParamConverter();

	@SuppressWarnings("unchecked")
	@Override
	public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
		if (Date.class.isAssignableFrom(rawType)) {
			return (ParamConverter<T>) CONVERTER;
		}
		return null;
	}

}
