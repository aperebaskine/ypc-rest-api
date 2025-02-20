package com.pinguela.ypc.rest.api.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.pinguela.yourpc.config.ConfigManager;

public class LocaleUtils {

	private static final String SUPPORTED_LOCALES_PNAME = "locale.supported";
	private static final String DEFAULT_LOCALE_PNAME = "locale.default";

	private static final Map<String, Locale> SUPPORTED_LOCALES = initLocaleMap();
	private static final Locale DEFAULT_LOCALE =
			Locale.forLanguageTag(ConfigManager.getParameter(DEFAULT_LOCALE_PNAME));

	private static final Map<String, Locale> initLocaleMap() {
		Map<String, Locale> localeMap = new HashMap<String, Locale>();
		String[] supportedLocales = ConfigManager.getParameters(SUPPORTED_LOCALES_PNAME);

		for (String languageTag : supportedLocales) {
			localeMap.put(languageTag, Locale.forLanguageTag(languageTag));
		}
		return Collections.unmodifiableMap(localeMap);
	}

	public static final boolean isSupported(String languageTag) {
		return SUPPORTED_LOCALES.containsKey(languageTag);
	}

	public static final boolean isSupported(Locale locale) {
		return isSupported(locale.toLanguageTag());
	}

	public static final Locale getDefault() {
		return DEFAULT_LOCALE;
	}

	public static final Locale getLocale(String localeStr) {
		return SUPPORTED_LOCALES.getOrDefault(localeStr, DEFAULT_LOCALE);
	}

	public static final Locale toLocale(String localeStr) {
		if (!isSupported(localeStr)) {
			throw new IllegalArgumentException(String.format("Unsupported locale %s.", localeStr));
		}
		return SUPPORTED_LOCALES.get(localeStr);
	}

}
