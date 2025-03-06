package com.pinguela.ypc.rest.api.json.param;

import org.apache.commons.lang3.function.FailableSupplier;

import com.pinguela.YPCException;
import com.pinguela.ypc.rest.api.model.ErrorLog;
import com.pinguela.ypc.rest.api.util.ResponseWrapper;
import com.pinguela.ypc.rest.api.validation.Validator;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

public class ParameterProcessor {
	
	private ErrorLog errorLog;
	
	private Status onNullStatus;
	private Status onErrorStatus;
	
	public ParameterProcessor() {
		this(Status.OK, Status.BAD_REQUEST);
	}
	
	public ParameterProcessor(Status onNullStatus, Status onErrorStatus) {
		this.errorLog = new ErrorLog();
		this.onNullStatus = onNullStatus;
		this.onErrorStatus = onErrorStatus;
	}
	
	@SafeVarargs
	public final <T> ParameterProcessor validate(String parameterName, T parameterValue, Validator<T>... validators) {
		if (validators.length < 1) {
			throw new IllegalArgumentException("Cannot call this function without validators.");
		}
		
		for (Validator<T> validator: validators) {
			if (!validator.test(parameterValue)) {
				errorLog.logError(parameterName, validator.getErrorCode());
			}
		}
		
		return this;
	}

	public Response buildResponse(FailableSupplier<?, YPCException> entitySupplier) {
		
		if (errorLog.hasErrors()) {
			return Response
					.status(Status.BAD_REQUEST)
					.entity(errorLog)
					.build();
		}
		
		return ResponseWrapper.wrap(entitySupplier, onNullStatus, onErrorStatus);
	}

}
