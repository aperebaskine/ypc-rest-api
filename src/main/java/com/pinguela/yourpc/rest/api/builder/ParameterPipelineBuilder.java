package com.pinguela.yourpc.rest.api.builder;

import java.util.LinkedList;
import java.util.function.Function;

import com.google.common.reflect.TypeToken;
import com.pinguela.yourpc.rest.api.param.ParameterPipeline;
import com.pinguela.ypc.param.processor.ParameterProcessor;

public class ParameterPipelineBuilder<F, R> {

	private TypeToken<R> returnType;
	private LinkedList<ParameterProcessor<?>> params;

	protected ParameterPipelineBuilder(TypeToken<R> returnType) {
		this.returnType = returnType;
	}

	public static <R> ParameterPipelineBuilder<R, R> newBuilder(Class<R> returnType) {
		return newBuilder(TypeToken.of(returnType));
	}

	public static <R> ParameterPipelineBuilder<R, R> newBuilder(TypeToken<R> returnType) {
		return new ParameterPipelineBuilder<R, R>(returnType);
	}

	@SuppressWarnings("unchecked")
	public <T> ParameterPipelineBuilder<Function<T, F>, R> param(ParameterProcessor<T> param) {
		this.params.add(param);
		return (ParameterPipelineBuilder<Function<T, F>, R>) this;
	}
	
	public ParameterPipeline<R> output(F function) {
		
	}

}
