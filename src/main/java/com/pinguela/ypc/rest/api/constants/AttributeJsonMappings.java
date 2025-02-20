package com.pinguela.ypc.rest.api.constants;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.pinguela.yourpc.model.constants.AttributeDataTypes;
import com.pinguela.yourpc.model.constants.AttributeValueHandlingModes;

public class AttributeJsonMappings {
	
	private static final BiMap<String, String> TYPE_MAPPINGS;
	private static final Map<Integer, String> HANDLING_MODE_MAPPINGS;
	
	static {
		TYPE_MAPPINGS = HashBiMap.create();
		TYPE_MAPPINGS.put(AttributeDataTypes.BIGINT, "int64");
		TYPE_MAPPINGS.put(AttributeDataTypes.VARCHAR, "string");
		TYPE_MAPPINGS.put(AttributeDataTypes.DECIMAL, "double");
		TYPE_MAPPINGS.put(AttributeDataTypes.BOOLEAN, "boolean");
		
		HANDLING_MODE_MAPPINGS = new HashMap<>();
		HANDLING_MODE_MAPPINGS.put(AttributeValueHandlingModes.RANGE, "range");
		HANDLING_MODE_MAPPINGS.put(AttributeValueHandlingModes.SET, "set");
	}
	
	public static String getJsonType(String dataType) {
		return TYPE_MAPPINGS.get(dataType);
	}
	
	public static String getDataType(String jsonType) {
		return TYPE_MAPPINGS.inverse().get(jsonType);
	}
	
	public static String getHandlingMode(Integer handlingMode) {
		return HANDLING_MODE_MAPPINGS.get(handlingMode);
	}
	
	public static Set<String> getAllJsonTypes() {
		return TYPE_MAPPINGS.values();
	}

}
