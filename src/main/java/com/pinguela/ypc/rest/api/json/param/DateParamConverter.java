package com.pinguela.ypc.rest.api.json.param;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.validator.GenericValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.ParamConverter;

public class DateParamConverter implements ParamConverter<Date> {
	
	private static Logger logger = LogManager.getLogger(DateParamConverter.class);
	
	private DateFormat format;
	
	public DateParamConverter() {
		format = new SimpleDateFormat("yyyy-MM-dd");
		format.setLenient(true);
	}

	@Override
	public Date fromString(String value) {
		
		if (!GenericValidator.isDate(value, Locale.ROOT)) {
			return null;
		}
		
		try {
			return format.parse(value);
		} catch (ParseException e) {
			logger.error(e);
			throw new WebApplicationException(String.format("Failed to parse date %s.", value));
		}
	}

	@Override
	public String toString(Date value) {
		return format.format(value);
	}

}
