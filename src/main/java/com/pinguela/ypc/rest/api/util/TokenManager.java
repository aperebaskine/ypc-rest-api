package com.pinguela.ypc.rest.api.util;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.pinguela.yourpc.model.User;
import com.pinguela.ypc.rest.api.exception.InvalidTokenException;
import com.pinguela.ypc.rest.api.model.UserPrincipal;

public class TokenManager {

	private static Logger logger = LogManager.getLogger(TokenManager.class);
	private static final TokenManager INSTANCE = new TokenManager();

	private final Algorithm algorithm;
	private final JWTVerifier verifier;

	private TokenManager() {
		KeyPairGenerator generator;

		try {
			generator = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			logger.fatal(e);
			throw new AssertionError(e);
		}

		generator.initialize(2048);
		KeyPair pair = generator.generateKeyPair();

		algorithm = Algorithm.RSA256((RSAPublicKey) pair.getPublic(), (RSAPrivateKey) pair.getPrivate());
		verifier = JWT.require(algorithm).build();
	}

	public static TokenManager getInstance() {
		return INSTANCE;
	}

	public String encodeToken(User user) {
		return JWT.create()
				.withSubject(user.getId().toString())
				.withClaim("role", user.getRoleId())
				.withClaim("name", user.getFirstName())
				.withClaim("fullName", user.getFullName())
				.withExpiresAt(Instant.now().plus(7, ChronoUnit.DAYS))
				.sign(algorithm);
	}

	public UserPrincipal decodeToken(String token) throws InvalidTokenException {

		DecodedJWT jwt;

		try {
			jwt = verifier.verify(token);
		} catch (JWTVerificationException e) {
			logger.warn(e);
			throw new InvalidTokenException(e);
		}

		Integer id = Integer.valueOf(jwt.getSubject());
		String name = jwt.getClaim("fullName").asString();
		String roleId = jwt.getClaim("role").asString();

		return new UserPrincipal(id, name, roleId);
	}

}
