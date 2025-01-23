package com.pinguela.ypc.param;

import java.util.Map.Entry;
import java.util.function.Predicate;

import javax.ws.rs.core.MultivaluedMap;

import com.google.common.reflect.TypeToken;

public class DefaultParameterProcessor<T>
extends ParameterProcessor<T> {
	
	public DefaultParameterProcessor(String name, TypeToken<T> type) {
		super(name, type);
	}

	@Override
	public T process(MultivaluedMap<String, String> parameterMap) {
		String valueStr = parameterMap.getFirst(getParameterName());
		T value = ParameterParser.parse(valueStr, getParameterType());
		
		for (Entry<Predicate<T>, String> validatorEntry : getValidators().entrySet()) {
			Predicate<T> validator = validatorEntry.getKey();
			if (!validator.test(value)) {
				
			}
		}
		
		return value;
	}

}
