package com.pinguela.ypc.rest.api.processing;

import java.util.List;
import java.util.function.Function;

import javax.ws.rs.core.MultivaluedMap;

import com.pinguela.atomize.model.Validator;
import com.pinguela.atomize.transform.BaseTransformer;

public class ParameterProcessor<T>
extends BaseTransformer<MultivaluedMap<String, String>, String, T> {
	
	private static Function<MultivaluedMap<String, String>, String> simpleExtractor(String key) {
		return (map) -> map.getFirst(key);
	}
	
	private static Function<MultivaluedMap<String, String>, List<String>> multiExtractor(String key) {
		return (map) -> map.get(key);
	}

	public ParameterProcessor(String name, Function<MultivaluedMap<String, String>, String> extractor,
			List<Validator<String>> preValidators, Function<String, T> transformer, List<Validator<T>> validators) {
		super(name, extractor, preValidators, transformer, validators);
	}

	public static <T> ParameterProcessorBuilder<T> of(String parameterName) {
		return new ParameterProcessorBuilder<T>(parameterName);
	}
	
}
