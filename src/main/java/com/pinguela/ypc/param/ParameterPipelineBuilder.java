package com.pinguela.ypc.param;

import java.util.LinkedList;
import java.util.function.Function;

import com.google.common.reflect.TypeToken;

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
