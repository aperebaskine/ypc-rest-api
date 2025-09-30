package com.pinguela.ypc.rest.api.internal;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.auth0.jwt.algorithms.Algorithm;

public class AlgorithmFactory {

	private static Logger logger = LogManager.getLogger(AlgorithmFactory.class);

	public static Algorithm createAlgorithm() {
		KeyPairGenerator generator;

		try {
			generator = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			logger.fatal(e);
			throw new AssertionError(e);
		}

		generator.initialize(2048);
		KeyPair pair = generator.generateKeyPair();

		return Algorithm.RSA256((RSAPublicKey) pair.getPublic(), (RSAPrivateKey) pair.getPrivate());
	}
	
	public static Algorithm createAlgorithm(RSAPublicKey publicKey) {
		return Algorithm.RSA256(publicKey, null);
	}

}
