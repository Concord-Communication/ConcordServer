package io.github.concord_communication.web_server.util;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Utilities for dealing with strings.
 */
public class StringUtils {
	/**
	 * Generates a random alphanumeric string.
	 * @param length The length of the string.
	 * @return The random string.
	 */
	public static String random(int length) {
		String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		char[] chars = new char[length];
		for (int i = 0; i < length; i++) {
			chars[i] = alphabet.charAt(ThreadLocalRandom.current().nextInt(alphabet.length()));
		}
		return new String(chars);
	}
}
