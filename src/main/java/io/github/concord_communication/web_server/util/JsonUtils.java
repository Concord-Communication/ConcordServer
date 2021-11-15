package io.github.concord_communication.web_server.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Some utilities for dealing with JSON data.
 */
public class JsonUtils {
	/**
	 * A public instance of {@link ObjectMapper} that can be used anywhere.
	 */
	public static final ObjectMapper JSON = new ObjectMapper();

	static {
		JSON.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
}
