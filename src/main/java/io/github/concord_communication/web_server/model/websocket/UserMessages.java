package io.github.concord_communication.web_server.model.websocket;

/**
 * Container for messages that are sent regarding the server's users.
 */
public final class UserMessages {
	// Hide constructor.
	private UserMessages() {}

	/**
	 * Sent when a user's public status has been updated.
	 */
	@MessageType("user_status_updated")
	public record StatusUpdated(long userId, String status) {}

	/**
	 * Sent when a new user joins the server.
	 */
	@MessageType("user_joined")
	public record Joined(long userId) {}

	/**
	 * Sent when a user leaves the server.
	 */
	@MessageType("user_left")
	public record Left(long userId) {}
}
