package com.pinguela.ypc.rest.api.util;

import org.apache.commons.lang3.function.FailableSupplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pinguela.YPCException;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import jakarta.ws.rs.core.Response.Status;

public class ResponseUtils {

	private static Logger logger = LogManager.getLogger(ResponseUtils.class);

	public static Response wrap(FailableSupplier<?, YPCException> supplier) {

		ResponseBuilder rb;
		Object o;

		try {
			o = supplier.get();
			
			if (o == null) {
				rb = Response.status(Status.BAD_REQUEST);
			} else {
				rb = Response.ok(o);
			}
		} catch (YPCException e) {
			logger.error("Exception thrown while attempting to fetch data for response: {}",
					e.getMessage(), e);
			rb = Response.status(Status.SERVICE_UNAVAILABLE);
		}

		return rb.build();
	}

}
