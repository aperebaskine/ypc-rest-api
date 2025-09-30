package com.pinguela.ypc.rest.api.constants;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum SessionType {
	CREDENTIALS("credentials"),
	OAUTH("oauth");

	private String name;

	private static final Map<String, SessionType> BY_NAME = new HashMap<>();

	static {
		for (SessionType type: values()) {
			BY_NAME.put(type.getName(), type);
		}
	}

	public static SessionType get(String name) {
		return Optional.ofNullable(BY_NAME.get(name))
				.orElseThrow(() -> new IllegalArgumentException(
						String.format("No valid session type with name %s.", name)
						));
	}

	SessionType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
