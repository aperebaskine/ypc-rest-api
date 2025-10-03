package com.pinguela.ypc.rest.api.model;

import java.util.ArrayList;
import java.util.List;

import com.pinguela.ypc.rest.api.cookies.CookieConfiguration;
import com.pinguela.ypc.rest.api.cookies.OAuthFlowCookie;
import com.pinguela.ypc.rest.api.util.HTTPUtils;

import jakarta.ws.rs.core.NewCookie;

public class OAuthResponseData {

	private String nextUrl;
	private List<NewCookie> cookies;

	public OAuthResponseData() {
		this.cookies = new ArrayList<NewCookie>();
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
		NewCookie newCookie = HTTPUtils.newCookieWithExpiry(config, value);
		cookies.add(newCookie);
		return this;
	}

	/**
	 * Add expired cookies for all cookies declared in {@link OAuthFlowCookie}.
	 */
	public OAuthResponseData withExpiredAuthFlowCookies() {
		for (OAuthFlowCookie cookie: OAuthFlowCookie.values()) {
			NewCookie newCookie = HTTPUtils.expiredCookie(cookie);
			cookies.add(newCookie);
		}
		return this;
	}

}
