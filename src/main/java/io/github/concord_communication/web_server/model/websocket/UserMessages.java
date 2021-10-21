package io.github.concord_communication.web_server.model.websocket;

public final class UserMessages {
	// Hide constructor.
	private UserMessages() {}

	@MessageType("user_status_updated")
	public record StatusUpdated(long userId, String status) {}

	@MessageType("user_joined")
	public record Joined(long userId) {}

	@MessageType("user_left")
	public record Left(long userId) {}
}
