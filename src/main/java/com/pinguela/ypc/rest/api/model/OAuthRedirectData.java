package com.pinguela.ypc.rest.api.model;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pinguela.ypc.rest.api.cookies.CookieConfiguration;
import com.pinguela.ypc.rest.api.cookies.OAuthFlowCookie;
import com.pinguela.ypc.rest.api.util.PathUtils;

import jakarta.ws.rs.core.NewCookie;

public class OAuthRedirectData {

	private String redirectUrl;
	private List<NewCookie> cookies;

	private URI baseUri;
	private Instant now;

	public OAuthRedirectData(URI baseUri) {
		this.cookies = new ArrayList<NewCookie>();
		this.baseUri = baseUri;
		this.now = Instant.now();
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public OAuthRedirectData withRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
		return this;
	}

	public List<NewCookie> getCookies() {
		return cookies;
	}

	public OAuthRedirectData withCookie(CookieConfiguration config, String value) {
		addCookie(config, value, false);
		return this;
	}

	/**
	 * Add expired cookies for all cookies declared in {@link OAuthFlowCookie}.
	 */
	public OAuthRedirectData withExpiredAuthFlowCookies() {
		for (OAuthFlowCookie cookie: OAuthFlowCookie.values()) {
			addCookie(cookie, redirectUrl, true);
		}
		return this;
	}
	
	private void addCookie(CookieConfiguration config, String value, boolean isExpired) {
		Date expiry = isExpired ? new Date(0) : Date.from(now.plusSeconds(config.getMaxAge()));
		NewCookie newCookie = new NewCookie.Builder(config.getName())
				.path(PathUtils.createPath(baseUri, config.getPath()))
				.value(value)
				.expiry(expiry)
				.httpOnly(config.isHttpOnly())
				.secure(config.isSecure())
				.sameSite(config.getSameSite())
				.build();

		cookies.add(newCookie);
	}

}
