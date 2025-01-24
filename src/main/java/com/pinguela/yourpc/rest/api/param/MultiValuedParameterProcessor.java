package com.pinguela.yourpc.rest.api.param;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

import com.google.common.reflect.TypeToken;

public class MultiValuedParameterProcessor<T>
extends ParameterProcessor<List<T>> {
	
	private 

	@SuppressWarnings("unchecked")
	MultiValuedParameterProcessor(String parameterName, TypeToken<T> parameterType) {
		super(parameterName, (TypeToken<List<T>>) TypeToken.of(new ParameterizedType() {
			
			private Type[] typeArguments = {parameterType.getType()};
 			
			@Override
			public Type getRawType() {
				return List.class;
			}
			
			@Override
			public Type getOwnerType() {
				return null;
			}
			
			@Override
			public Type[] getActualTypeArguments() {
				return typeArguments;
			}
		}));
	}
	
	@Override
	public ParameterProcessor<List<T>> required() {
		// TODO Auto-generated method stub
		return super.required();
	}
	
	@Override
	public List<T> process(MultivaluedMap<String, String> parameterMap) {
		List<String> param = parameterMap.get(getParameterName());
	}

}
