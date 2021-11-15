package io.github.concord_communication.web_server.model.websocket;

import io.github.concord_communication.web_server.model.user.User;

/**
 * Generic class for any message that's received from a client.
 */
public record ClientMessageEvent(User user, String type, Object message) {
	/**
	 * Convenience method for obtaining the body of the event as a certain type,
	 * while ignoring type-casting safety.
	 * @param <T> The type of the body.
	 * @return The body of the message.
	 */
	@SuppressWarnings("unchecked")
	public <T> T getMessage() {
		return (T) message;
	}
}
