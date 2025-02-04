package com.pinguela.ypc.rest.api.processing;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Function;

import javax.ws.rs.core.MultivaluedMap;

import com.google.common.reflect.TypeToken;
import com.pinguela.atomize.builder.PipelineBuilder;

import io.swagger.v3.core.util.ParameterProcessor;

public class ParamPipelineBuilder<F, R> {

	@SuppressWarnings("unchecked")
	private static TypeToken<MultivaluedMap<String, String>> sourceType =
	(TypeToken<MultivaluedMap<String, String>>) TypeToken.of(new ParameterizedType() {

		Type[] typeArgs = {String.class, String.class};

		@Override
		public Type getRawType() {
			return MultivaluedMap.class;
		}

		@Override
		public Type getOwnerType() {
			return null;
		}

		@Override
		public Type[] getActualTypeArguments() {
			return typeArgs;
		}
	});

	private PipelineBuilder<MultivaluedMap<String, String>, F, R> builder;

	@SuppressWarnings("unchecked")
	private ParamPipelineBuilder(TypeToken<R> returnType) {
		this.builder = (PipelineBuilder<MultivaluedMap<String, String>, F, R>)
				PipelineBuilder.newBuilder(sourceType, returnType);
	}

	public static <R> ParamPipelineBuilder<R, R> newBuilder(Class<R> returnType) {
		return new ParamPipelineBuilder<R, R>(TypeToken.of(returnType));
	}

	public static <R> ParamPipelineBuilder<R, R> newBuilder(TypeToken<R> returnType) {
		return new ParamPipelineBuilder<R, R>(returnType);
	}

	public ParamPipelineBuilder<R> param(ParameterProcessor<R> processor) {

	}

}
