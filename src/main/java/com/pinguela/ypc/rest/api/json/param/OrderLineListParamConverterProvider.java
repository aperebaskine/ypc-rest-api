package com.pinguela.ypc.rest.api.json.param;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.pinguela.yourpc.model.OrderLine;
import com.sun.xml.bind.v2.schemagen.xmlschema.List;

import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import jakarta.ws.rs.ext.Provider;

@Provider
public class OrderLineListParamConverterProvider implements ParamConverterProvider {

	private OrderLineListParamConverter converter = new OrderLineListParamConverter();

	@SuppressWarnings("unchecked")
	@Override
	public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {

		for (Annotation annotation : annotations) {
			if (annotation instanceof FormParam
					&& isSupportedType(genericType)) {
				return (ParamConverter<T>) converter;
			}
		}

		return null;
	}

	private static boolean isSupportedType(Type type) {

		if (!(type instanceof ParameterizedType)) {
			return false;
		}

		ParameterizedType pType = (ParameterizedType) type;

		if (!List.class.isAssignableFrom(List.class)) {
			return false;
		}

		Type typeParameter = pType.getActualTypeArguments()[0];

		return typeParameter instanceof Class
				&& OrderLine.class.isAssignableFrom(
						(Class<?>) typeParameter
						);
	}

}
