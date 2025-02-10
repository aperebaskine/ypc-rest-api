package com.pinguela.ypc.rest.api.constants;

import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.pinguela.yourpc.model.constants.AttributeDataTypes;

public class AttributeJsonTypes {
	
	private static final BiMap<String, String> TYPE_MAPPINGS;
	
	static {
		TYPE_MAPPINGS = HashBiMap.create();
		TYPE_MAPPINGS.put(AttributeDataTypes.BIGINT, "int64");
		TYPE_MAPPINGS.put(AttributeDataTypes.VARCHAR, "string");
		TYPE_MAPPINGS.put(AttributeDataTypes.DECIMAL, "double");
		TYPE_MAPPINGS.put(AttributeDataTypes.BOOLEAN, "boolean");
	}
	
	public static String getJsonType(String dataType) {
		return TYPE_MAPPINGS.get(dataType);
	}
	
	public static String getDataType(String jsonType) {
		return TYPE_MAPPINGS.inverse().get(jsonType);
	}
	
	public static Set<String> getAllJsonTypes() {
		return TYPE_MAPPINGS.values();
	}

}
