package com.pinguela.ypc.rest.api.schema;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.type.SimpleType;
import com.pinguela.yourpc.model.dto.AttributeDTO;
import com.pinguela.yourpc.model.dto.AttributeValueDTO;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.Schema;

public class AttributeValueModelConverter implements ModelConverter {

	@Override
	public Schema<?> resolve(AnnotatedType type, ModelConverterContext context, Iterator<ModelConverter> chain) {

		Type t = type.getType();

		if (t instanceof SimpleType && AttributeValueDTO.class.equals(((SimpleType) t).getRawClass())) {
			return createSchema(context);
		}

		return (chain.hasNext() ? chain.next().resolve(type, context, chain) : null);
	}

	private Schema<?> createSchema(ModelConverterContext context) {
		
		Schema<?> attributeValueSchema = new Schema<>()
				.addProperty("id", new IntegerSchema().format("int64").example(42l));
		
		@SuppressWarnings("rawtypes")
		List<Schema> valueTypes = new ArrayList<>();
		
		for (Class<?> valueType : AttributeDTO.TYPE_PARAMETER_CLASSES.values()) {
			valueTypes.add(context.resolve(new AnnotatedType(valueType)));
		}

		Schema<?> valueTypeSchema = new Schema<>();
		valueTypeSchema.setOneOf(valueTypes);
		
		attributeValueSchema.addProperty("value", valueTypeSchema);
		
		return attributeValueSchema;
	}

}
