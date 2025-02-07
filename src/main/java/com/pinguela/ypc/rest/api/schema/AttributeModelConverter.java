package com.pinguela.ypc.rest.api.schema;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.type.SimpleType;
import com.pinguela.yourpc.model.dto.AttributeDTO;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;

public class AttributeModelConverter implements ModelConverter {

	@Override
	public Schema<?> resolve(AnnotatedType type, ModelConverterContext context, Iterator<ModelConverter> chain) {

		Type t = type.getType();

		if (t instanceof SimpleType && AttributeDTO.class.equals(((SimpleType) t).getRawClass())) {
			return createSchema(context);
		}

		return (chain.hasNext() ? chain.next().resolve(type, context, chain) : null);
	}

	private Schema<?> createSchema(ModelConverterContext context) {
		
		Schema<?> attributeSchema = new Schema<>()
				.addProperty("id", new IntegerSchema().format("int64").example(42l))
				.addProperty("name", new StringSchema().example("name"))
				.addProperty("dataType", new StringSchema().example("INT"));
		
		Schema<?> attributeValueSchema = new Schema<>()
				.addProperty("id", new IntegerSchema().format("int64").example(42l));
		
		@SuppressWarnings("rawtypes")
		List<Schema> valueTypes = new ArrayList<>();
		
		for (Class<?> valueType : AttributeDTO.TYPE_PARAMETER_CLASSES.values()) {
			valueTypes.add(context.resolve(new AnnotatedType(valueType)));
		}

		Schema<?> oneOfSchema = new Schema<>();
		oneOfSchema.setOneOf(valueTypes);
		
		attributeValueSchema.addProperty("value", oneOfSchema);
		attributeSchema.addProperty("values", attributeValueSchema);
		
		return attributeSchema;
	}

}
