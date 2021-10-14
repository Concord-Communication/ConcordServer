package io.github.concord_communication.web_server.model.websocket;

import io.github.concord_communication.web_server.model.User;

public record ClientMessageEvent(User user, String type, Object message) {

	@SuppressWarnings("unchecked")
	public <T> T getMessage() {
		return (T) message;
	}
}
