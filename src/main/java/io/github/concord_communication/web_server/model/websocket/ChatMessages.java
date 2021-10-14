package io.github.concord_communication.web_server.model.websocket;

import io.github.concord_communication.web_server.model.Chat;

/**
 * This class contains definitions for websocket messages that are sent to
 * communicate about updates to the chat in various channels.
 */
public final class ChatMessages {
	// Hide the constructor.
	private ChatMessages() {}

	/**
	 * A message that's sent by connected clients when they write a new chat.
	 */
	@MessageType("chat_written")
	public record Written(long channelId, Long threadId, long sentAt, String content) {}

	/**
	 * A message that clients send to indicate that they're typing in a certain
	 * channel and/or thread. This message is then forwarded by the server to
	 * all connected clients. Clients don't need to provide a userId.
	 */
	@MessageType("chat_typing")
	public record Typing(Long userId, long channelId, Long threadId, long sentAt) {}

	/**
	 * A message that's sent by the server to all connected clients when a new
	 * chat message is sent.
	 */
	@MessageType("chat_sent")
	public record Sent(long id, long authorId, long channelId, Long threadId, long createdAt, String content) {
		public Sent(Chat c) {
			this(c.getId(), c.getAuthorId(), c.getChannelId(), c.getThreadId(), c.getCreatedAt(), c.getContent());
		}
	}

	/**
	 * A message that's sent by the server to all connected clients when a chat
	 * message is deleted.
	 */
	@MessageType("chat_deleted")
	public record Deleted(long id) {}

	/**
	 * A message that's sent by the server to all connected clients when a chat
	 * message is edited by its author.
	 */
	@MessageType("chat_updated")
	public record Updated(long id, String content) {}
}
