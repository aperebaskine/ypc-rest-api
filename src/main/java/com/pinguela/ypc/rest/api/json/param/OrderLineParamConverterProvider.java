package com.pinguela.ypc.rest.api.json.param;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import com.pinguela.yourpc.model.OrderLine;

import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import jakarta.ws.rs.ext.Provider;

@Provider
public class OrderLineParamConverterProvider implements ParamConverterProvider {
	
	private OrderLineParamConverter converter = new OrderLineParamConverter();

	@SuppressWarnings("unchecked")
	@Override
	public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
		if (OrderLine.class.isAssignableFrom(rawType)) {
			return (ParamConverter<T>) converter;
		}
		
		return null;
	}

}
