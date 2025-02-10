package com.pinguela.ypc.rest.api.json.serialize;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

@SuppressWarnings({ "serial", "rawtypes" })
public class MapToValueArraySerializer extends StdSerializer<Map> {

	public MapToValueArraySerializer() {
		super(Map.class);
	}

	@Override
	public void serialize(Map map, JsonGenerator gen,
			SerializerProvider provider) throws IOException {
		gen.writeObject(map.values());
	}

}
