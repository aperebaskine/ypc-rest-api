package com.pinguela.ypc.rest.api.model;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.pinguela.yourpc.model.User;
import com.pinguela.ypc.rest.api.constants.SessionType;
import com.pinguela.ypc.rest.api.exception.InvalidTokenException;
import com.pinguela.ypc.rest.api.internal.AlgorithmFactory;

public class Session {

	private static Logger logger = LogManager.getLogger(Session.class);

	private static final Algorithm ALGORITHM = AlgorithmFactory.createAlgorithm();
	private static final JWTVerifier VERIFIER = JWT.require(ALGORITHM)
			.acceptLeeway(60)
			.build();

	private String id;
	private UserPrincipal user;
	private SessionType type;
	private Map<String, Object> properties;

	public Session(User user, SessionType type) {
		this(new UserPrincipal(user), type);
	}

	public Session(UserPrincipal user, SessionType type) {
		this(UUID.randomUUID().toString(), user, type);
	}

	private Session(String id, UserPrincipal user, SessionType type) {
		this.id = id;
		this.user = user;
		this.type = type;
		this.properties = new HashMap<String, Object>();
	}

	public String getId() {
		return id;
	}

	public UserPrincipal getUser() {
		return user;
	}

	public SessionType getType() {
		return type;
	}

	public String getProperty(String name) {
		return properties.get(name).toString();
	}

	public void setProperty(String name, String value) {
		properties.put(name, value);
	}

	public String encode(Duration duration) {
		Builder builder = JWT.create()
				.withJWTId(UUID.randomUUID().toString())
				.withSubject(user.getId().toString())
				.withClaim("user_name", user.getName())
				.withClaim("user_role", user.getRole())
				.withClaim("user_email", user.getEmail())
				.withClaim("session_id", id)
				.withClaim("session_type", type.getName())
				.withClaim("properties", properties)
				.withExpiresAt(Instant.now().plus(duration));

		return builder.sign(ALGORITHM);
	}

	public static Session decode(String token) throws InvalidTokenException {
		DecodedJWT jwt;
		try {
			jwt = VERIFIER.verify(token);
		} catch (JWTVerificationException e) {
			logger.error(e);
			throw new InvalidTokenException(e);
		}

		String id = jwt.getClaim("session_id").asString();
		UserPrincipal user = new UserPrincipal(
				Integer.valueOf(jwt.getSubject()),
				jwt.getClaim("user_name").asString(),
				jwt.getClaim("user_role").asString(),
				jwt.getClaim("user_email").asString()
				);
		String type = jwt.getClaim("session_type").asString();
		SessionType sessionType = SessionType.get(type);

		Session session = new Session(id, user, sessionType);
		session.properties = jwt.getClaim("properties").asMap();

		return session;
	}

}
