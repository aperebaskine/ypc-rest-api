package com.pinguela.ypc.rest.api.validation;

import java.util.Locale;

import com.pinguela.YPCException;
import com.pinguela.yourpc.model.constants.AttributeValueHandlingModes;
import com.pinguela.yourpc.model.dto.AttributeDTO;
import com.pinguela.yourpc.model.dto.AttributeValueDTO;
import com.pinguela.yourpc.service.AttributeService;
import com.pinguela.yourpc.service.impl.AttributeServiceImpl;

// TODO: Cache values
public class AttributeRangeValidator {

	private static AttributeRangeValidator INSTANCE = new AttributeRangeValidator();

	public static AttributeRangeValidator getInstance() {
		return INSTANCE;
	}

	private AttributeService service;

	public AttributeRangeValidator() {
		service = new AttributeServiceImpl();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean validate(AttributeDTO<?> dto, Short categoryId) throws YPCException {
		
		if (dto.getValueHandlingMode() != AttributeValueHandlingModes.RANGE) {
			throw new IllegalArgumentException("Attribute value handling mode is not RANGE, cannot validate.");
		}
		AttributeDTO<?> dbData = service.findById(dto.getId(), Locale.forLanguageTag("en-GB"), false, categoryId);
		
		if (dbData == null) {
			return false;
		}
		
		Comparable min = (Comparable) dto.getValueAt(0);
		Comparable max = (Comparable) dto.getValueAt(dto.valueCount() -1);
		
		if (min.compareTo(max) > 0) {
			return false;
		}
		
		Comparable dbMin = (Comparable) dbData.getValueAt(0);
		Comparable dbMax = (Comparable) dbData.getValueAt(dbData.valueCount() -1);
		
		// Clamp values in criteria DTO
		if (dbMin.compareTo(min) > 0) {
			dto.getValues().add(0, (AttributeValueDTO) dbData.getValues().get(0));
		}
		
		if (dbMax.compareTo(max) < 0) {
			dto.getValues().add(dto.valueCount() -1, (AttributeValueDTO) dbData.getValues().get(dbData.valueCount() -1));
		}
		
		return min.compareTo(dbMin) > 0 || max.compareTo(dbMax) < 0;
	}
}
