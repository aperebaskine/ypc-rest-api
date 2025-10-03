package com.pinguela.ypc.rest.api.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pinguela.ypc.rest.api.cookies.CookieConfiguration;
import com.pinguela.ypc.rest.api.cookies.OAuthFlowCookie;
import com.pinguela.ypc.rest.api.util.PathUtils;

import jakarta.ws.rs.core.NewCookie;

public class OAuthResponseData {

	private String nextUrl;
	private List<NewCookie> cookies;

	private Instant now;

	public OAuthResponseData() {
		this.cookies = new ArrayList<NewCookie>();
		this.now = Instant.now();
	}

	public String getUrl() {
		return nextUrl;
	}

	public OAuthResponseData withUrl(String nextUrl) {
		this.nextUrl = nextUrl;
		return this;
	}

	public List<NewCookie> getCookies() {
		return cookies;
	}

	public OAuthResponseData withCookie(CookieConfiguration config, String value) {
		addCookie(config, value, false);
		return this;
	}

	/**
	 * Add expired cookies for all cookies declared in {@link OAuthFlowCookie}.
	 */
	public OAuthResponseData withExpiredAuthFlowCookies() {
		for (OAuthFlowCookie cookie: OAuthFlowCookie.values()) {
			addCookie(cookie, nextUrl, true);
		}
		return this;
	}
	
	private void addCookie(CookieConfiguration config, String value, boolean isExpired) {
		Date expiry = isExpired ? new Date(0) : Date.from(now.plusSeconds(config.getMaxAge()));
		NewCookie newCookie = new NewCookie.Builder(config.getName())
				.path(PathUtils.createPath(config.getPath()))
				.value(value)
				.expiry(expiry)
				.httpOnly(config.isHttpOnly())
				.secure(config.isSecure())
				.sameSite(config.getSameSite())
				.build();

		cookies.add(newCookie);
	}

}
