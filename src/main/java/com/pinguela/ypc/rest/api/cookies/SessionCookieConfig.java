package com.pinguela.ypc.rest.api.cookies;

import com.pinguela.ypc.rest.api.constants.Paths;

import jakarta.ws.rs.core.NewCookie.SameSite;

public final class SessionCookieConfig implements CookieConfiguration {
	
	private static final SessionCookieConfig INSTANCE = new SessionCookieConfig();

	private SessionCookieConfig() {
	}

	public static SessionCookieConfig getInstance() {
		return INSTANCE;
	}
	
	@Override
	public String getName() {
		return "__Secure-session";
	}
	
	@Override
	public String getPath() {
		return Paths.SESSION;
	}

	@Override
	public int getMaxAge() {
		return 604800;
	}

	@Override
	public boolean isHttpOnly() {
		return true;
	}

	@Override
	public boolean isSecure() {
		return true;
	}

	@Override
	public SameSite getSameSite() {
		return SameSite.STRICT;
	}

}
