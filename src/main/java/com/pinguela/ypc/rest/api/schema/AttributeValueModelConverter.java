package com.pinguela.ypc.rest.api.schema;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.pinguela.yourpc.model.dto.AttributeDTO;
import com.pinguela.yourpc.model.dto.AttributeValueDTO;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;

public class AttributeValueModelConverter implements ModelConverter {

	@Override
	public Schema<?> resolve(AnnotatedType type, ModelConverterContext context, Iterator<ModelConverter> chain) {

		if (isSupportedType(type)) {
			return createSchema(context);
		}

		return (chain.hasNext() ? chain.next().resolve(type, context, chain) : null);
	}

	private Schema<?> createSchema(ModelConverterContext context) {
		
		Schema<?> attributeValueSchema = new ObjectSchema()
				.name("AttributeValue")
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
	
	private boolean isSupportedType(AnnotatedType type) {
		Type t = type.getType();
		return t instanceof Class && AttributeValueDTO.class.isAssignableFrom((Class<?>) t);
	}

}
