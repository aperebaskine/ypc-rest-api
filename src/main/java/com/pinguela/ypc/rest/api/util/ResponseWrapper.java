package com.pinguela.ypc.rest.api.util;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang3.function.FailableSupplier;
import org.apache.commons.validator.GenericValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pinguela.DataException;
import com.pinguela.YPCException;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import jakarta.ws.rs.core.Response.Status;

public class ResponseWrapper {

	private static Logger logger = LogManager.getLogger(ResponseWrapper.class);
	
	public static Response wrap(FailableSupplier<?, YPCException> serviceCaller) {
		return wrap(serviceCaller, Status.OK, Status.INTERNAL_SERVER_ERROR);
	}
	
	public static Response wrap(FailableSupplier<?, YPCException> serviceCaller, Status onNull) {
		return wrap(serviceCaller, onNull, Status.INTERNAL_SERVER_ERROR);
	}

	public static Response wrap(FailableSupplier<?, YPCException> serviceCaller, Status onNull, Status onError) {

		ResponseBuilder rb;
		Object o;

		try {
			o = serviceCaller.get();
			
			if (isNullOrEmpty(o)) {
				rb = Response.status(onNull);
			} else {
				rb = Response.ok(o);
			}
		} catch (DataException e) {
			logger.error("Exception thrown by data layer: {}", e.getMessage(), e);
			rb = Response.status(Status.BAD_REQUEST);
		} catch (RuntimeException e) {
			logger.error("Exception thrown by service layer: {}", e.getMessage(), e);
			rb = Response.status(Status.BAD_REQUEST);
		} catch (YPCException e) {
			logger.error("Exception thrown: {}", e.getMessage(), e);
			rb = Response.status(onError);
		}

		return rb.build();
	}
	
	// TODO: Implement a strategy pattern here
	private static boolean isNullOrEmpty(Object object) {
		
		if (object == null) {
			return true;
		}
		
		if (object instanceof String) {
			return GenericValidator.isBlankOrNull((String) object);
		}
		
		if (object instanceof Collection) {
			return ((Collection<?>) object).isEmpty();
		} 
		
		if (object instanceof Map) {
			return ((Map<?, ?>) object).isEmpty();
		}
		
		return false;
		
	}

}
