package com.pinguela.ypc.rest.api.validation;

import java.util.function.Predicate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pinguela.DataException;
import com.pinguela.ServiceException;
import com.pinguela.yourpc.service.CustomerService;
import com.pinguela.yourpc.service.impl.CustomerServiceImpl;
import com.pinguela.ypc.rest.api.constants.ErrorCodes;

import jakarta.ws.rs.WebApplicationException;

public class Validators {
	
	private static Logger logger = LogManager.getLogger(Validators.class);
	
	public static Validator<String> isUnusedEmail() {
		return Validator.of(new Predicate<String>() {
			
			private CustomerService customerService = new CustomerServiceImpl();
			
			@Override
			public boolean test(String t) {
				try {
					return customerService.findByEmail(t) == null;
				} catch (ServiceException | DataException e) {
					logger.error(e.getMessage(), e);
					throw new WebApplicationException(e);
				}
			}
		}, ErrorCodes.EMAIL_IN_USE);
	}

}
