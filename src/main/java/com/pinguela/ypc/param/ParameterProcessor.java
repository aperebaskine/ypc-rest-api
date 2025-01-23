package com.pinguela.ypc.param;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import javax.ws.rs.core.MultivaluedMap;

import com.google.common.reflect.TypeToken;

public abstract class ParameterProcessor<T> {

	private String parameterName;
	private TypeToken<T> parameterType;
	private boolean isRequired = false;
	private Map<Predicate<T>, String> validators;

	protected ParameterProcessor(String parameterName, TypeToken<T> parameterType) {
		this.parameterName = parameterName;
		this.parameterType = parameterType;
		this.validators = new LinkedHashMap<Predicate<T>, String>();
	}

	public static <T> ParameterProcessor<T> of(String name, Class<T> type) {
		return of(name, TypeToken.of(type));
	}

	public static <T> ParameterProcessor<T> of(String name, TypeToken<T> type) {
		
	}

	public static <T> ParameterProcessor<List<T>> multiValued(String name, Class<T> type) {
		return multiValued(name, TypeToken.of(type));
	}
	
	public static <T> ParameterProcessor<List<T>> multiValued(String name, TypeToken<T> type) {
		return new MultiValuedParameterProcessor();
	}
	
	public ParameterProcessor<T> required() {
		this.isRequired = true;
		return this;
	}
	
	protected String getParameterName() {
		return parameterName;
	}
	
	protected TypeToken<T> getParameterType() {
		return parameterType;
	}
	
	protected boolean isRequired() {
		return isRequired;
	}
	
	protected Map<Predicate<T>, String> getValidators() {
		return validators;
	}
	
	public abstract T process(MultivaluedMap<String, String> parameterMap, ErrorLog errorLog);
	
}
