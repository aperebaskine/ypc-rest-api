package com.pinguela.ypc.rest.api.util;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.function.FailableSupplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pinguela.YPCException;

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
			rb = Response.status(Status.INTERNAL_SERVER_ERROR);
		}

		return rb.build();
	}

}
