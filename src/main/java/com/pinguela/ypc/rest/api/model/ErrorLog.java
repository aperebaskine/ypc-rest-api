package com.pinguela.ypc.rest.api.model;

import java.util.HashMap;
import java.util.Map;

public class ErrorLog {
	
	private Map<String, String> errors;
	
	public ErrorLog() {
		errors = new HashMap<String, String>();
	}
	
	public void logError(String key, String errorCode) {
		this.errors.put(key, errorCode);
	}
	
	public boolean hasErrors() {
		return !errors.isEmpty();
	}

}
