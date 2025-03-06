package com.pinguela.ypc.rest.api.validation;

import java.util.function.Predicate;

public class Validator<T> {
	
	private Predicate<T> predicate;
	private String errorCode;
	
	private Validator(Predicate<T> predicate, String errorCode) {
		super();
		this.predicate = predicate;
		this.errorCode = errorCode;
	}

	public static <T> Validator<T> of(Predicate<T> predicate, String errorCode) {
		return new Validator<T>(predicate, errorCode);
	}
	
	public boolean test(T t) {
		return predicate.test(t);
	}
	
	public String getErrorCode() {
		return errorCode;
	}

}
