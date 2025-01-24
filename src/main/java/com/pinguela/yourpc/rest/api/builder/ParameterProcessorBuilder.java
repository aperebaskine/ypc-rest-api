package com.pinguela.yourpc.rest.api.builder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import com.google.common.reflect.TypeToken;
import com.pinguela.yourpc.rest.api.param.ParameterProcessor;

public abstract class ParameterProcessorBuilder<R> {

	private String parameterName;
	private TypeToken<R> returnType;
	private Map<Predicate<R>, String> validators;

	protected ParameterProcessorBuilder(String parameterName, TypeToken<R> returnType) {
		super();
		this.parameterName = parameterName;
		this.returnType = returnType;
		this.validators = new LinkedHashMap<Predicate<R>, String>();
	}

	public static <R> ParameterProcessorBuilder<R> of(String parameterName, TypeToken<R> returnType) {
		return new ParameterProcessorBuilder<>(parameterName, returnType, false);
	}

	public static <R> ParameterProcessorBuilder<List<R>> multiValued(String parameterName, TypeToken<R> returnType) {
		return new ParameterProcessorBuilder<>(parameterName, getListTypeToken(returnType), true);
	}

	@SuppressWarnings("unchecked")
	private static <R> TypeToken<List<R>> getListTypeToken(TypeToken<R> returnType) {
		return (TypeToken<List<R>>) TypeToken.of(new ParameterizedType() {

			private Type[] typeArguments = {returnType.getType()};

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
		});
	}

	public String getParameterName() {
		return parameterName;
	}

	public TypeToken<R> getReturnType() {
		return returnType;
	}

	public boolean isRequired() {
		return isRequired;
	}

	public Map<Predicate<R>, String> getValidators() {
		return validators;
	}

	@SuppressWarnings("unchecked")
	public ParameterProcessorBuilder<R> required() {
		this.isRequired = true;
		return this;
	}

	@SuppressWarnings("unchecked")
	public ParameterProcessorBuilder<R> validated(Predicate<R> validator, String errorCode) {
		validators.put(validator, errorCode);
		return this;
	}

	public ParameterProcessor<R> build() {
		// TODO
	}
	
	public class SinglyValuedParameterProcessorBuilder extends ParameterProcessorBuilder<R> {

		private boolean isRequired;
		
		protected SinglyValuedParameterProcessorBuilder(String parameterName, TypeToken<R> returnType) {
			super(parameterName, returnType);
		}
		
		public 
		
	}

}
