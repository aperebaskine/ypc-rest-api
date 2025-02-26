package com.pinguela.ypc.rest.api.schema;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.pinguela.yourpc.model.dto.AttributeDTO;
import com.pinguela.yourpc.model.dto.AttributeValueDTO;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;

@SuppressWarnings("rawtypes")
public class AttributeValueModelConverter implements ModelConverter {

	public static final String X_SCHEMA_REPRESENTATION = "x-schema-representation";

	public static final String REPRESENTATION_FORMAT = "format";

	public static final String COMPLETE_FORMAT = "complete";
	public static final String COMPACT_FORMAT = "compact";

	public static final String ATTRIBUTE_VALUE_NAME = "AttributeValue";
	public static final String COMPACT_ATTRIBUTE_VALUE_NAME = "LightAttributeValue";

	@Override
	public Schema<?> resolve(AnnotatedType type, ModelConverterContext context, Iterator<ModelConverter> chain) {

		if (isSupportedType(type)) {
			switch (getFormat(type)) {
			case COMPACT_FORMAT:
				return createCompactSchema(context);
			case COMPLETE_FORMAT:
				return createCompleteSchema(context);
			}
		}

		return (chain.hasNext() ? chain.next().resolve(type, context, chain) : null);
	}

	private Schema<?> createCompactSchema(ModelConverterContext context) {
		Schema<?> schema = new ObjectSchema()
				.name(COMPACT_ATTRIBUTE_VALUE_NAME);

		List<Schema> valueTypes = getValueTypes(context);
		schema.setOneOf(valueTypes);
				
		return schema;
	}

	private Schema<?> createCompleteSchema(ModelConverterContext context) {

		Schema<?> attributeValueSchema = new ObjectSchema()
				.name(ATTRIBUTE_VALUE_NAME)
				.addProperty("id", new IntegerSchema().format("int64"));

		List<Schema> valueTypes = getValueTypes(context);
		Schema<?> oneOfSchema = new Schema<>();
		oneOfSchema.setOneOf(valueTypes);

		attributeValueSchema.addProperty("value", oneOfSchema);

		return attributeValueSchema;
	}

	private List<Schema> getValueTypes(ModelConverterContext context) {

		List<Schema> valueTypes = new ArrayList<>();

		for (Class<?> valueType : AttributeDTO.TYPE_PARAMETER_CLASSES.values()) {
			valueTypes.add(context.resolve(new AnnotatedType(valueType)));
		}

		return valueTypes;
	}

	private String getFormat(AnnotatedType type) {

		if (type.getCtxAnnotations() == null) {
			return COMPLETE_FORMAT;
		}

		for (Annotation annotation : type.getCtxAnnotations()) {

			if (!(annotation instanceof io.swagger.v3.oas.annotations.media.Schema)) {
				continue;
			}

			Extension[] extensions = (Extension[]) ((io.swagger.v3.oas.annotations.media.Schema) annotation).extensions();

			for (Extension extension : extensions) {
				if (!X_SCHEMA_REPRESENTATION.equals(extension.name())) {
					continue;
				}

				for (ExtensionProperty property : extension.properties()) {
					if (REPRESENTATION_FORMAT.equals(property.name())) {
						return property.value();
					}
				}
			}			
		}

		return COMPLETE_FORMAT;
	}

	private boolean isSupportedType(AnnotatedType type) {
		Type t = type.getType();
		return t instanceof Class && AttributeValueDTO.class.isAssignableFrom((Class<?>) t);
	}

}
