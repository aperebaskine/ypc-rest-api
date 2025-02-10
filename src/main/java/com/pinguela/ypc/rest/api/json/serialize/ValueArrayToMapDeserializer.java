package com.pinguela.ypc.rest.api.json.serialize;

import java.io.IOException;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.type.TypeFactory;

@SuppressWarnings("serial")
public abstract class ValueArrayToMapDeserializer<K, V> extends StdDeserializer<Map<K, V>> {

	private final Supplier<Map<K, V>> mapSupplier;
	private final TypeReference<V> valueTypeRef;
	private final Function<V, K> keyExtractor;

	protected ValueArrayToMapDeserializer(Supplier<Map<K, V>> mapSupplier, 
			TypeReference<K> keyTypeRef, TypeReference<V> valueTypeRef, Function<V, K> keyExtractor) {
		super(TypeFactory.defaultInstance().constructMapType(
				mapSupplier.get().getClass(), 
				TypeFactory.defaultInstance().constructType(keyTypeRef),
				TypeFactory.defaultInstance().constructType(valueTypeRef))
				);
		this.mapSupplier = mapSupplier;
		this.valueTypeRef = valueTypeRef;
		this.keyExtractor = keyExtractor;
	}

	@Override
	public Map<K, V> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
		
		Map<K, V> map = mapSupplier.get();

		while (p.nextToken() != JsonToken.END_ARRAY) {
			V v = p.readValueAs(valueTypeRef);
			K k = keyExtractor.apply(v);
			map.put(k, v);
		}

		return map;
	}

}
