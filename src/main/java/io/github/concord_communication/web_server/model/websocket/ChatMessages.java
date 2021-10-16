package io.github.concord_communication.web_server.model.websocket;

import io.github.concord_communication.web_server.api.dto.ChatResponse;
import io.github.concord_communication.web_server.model.Chat;

import java.util.Map;

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
	 * A message that is sent by a client to indicate they are adding a reaction
	 * to a chat message (or removing it if adding is false).
	 */
	@MessageType("chat_reaction")
	public record Reaction(long chatId, String reaction, boolean adding) {}

	/**
	 * A message that's sent by the server to all connected clients when a new
	 * chat message is sent.
	 */
	@MessageType("chat_sent")
	public record Sent(ChatResponse chat) {
		public Sent(Chat c) {
			this(new ChatResponse(c));
		}
	}

	/**
	 * A message that's sent by the server to all connected clients when a chat
	 * message is deleted.
	 */
	@MessageType("chat_deleted")
	public record Deleted(long chatId, long channelId, Long threadId) {
		public Deleted(Chat c) {
			this(c.getId(), c.getChannelId(), c.getThreadId());
		}
	}

	/**
	 * A message that's sent by the server to all connected clients when a chat
	 * message is edited by its author.
	 */
	@MessageType("chat_edited")
	public record Edited(long chatId, long channelId, Long threadId, String content) {
		public Edited(Chat c) {
			this(c.getId(), c.getChannelId(), c.getThreadId(), c.getContent());
		}
	}

	/**
	 * A message that's sent by the server to all connected clients when a chat
	 * message's reactions are updated.
	 */
	@MessageType("chat_reactions_updated")
	public record ReactionsUpdated(long chatId, long channelId, Long threadId, Map<String, Integer> reactions) {
		public ReactionsUpdated(Chat c) {
			this(c.getId(), c.getChannelId(), c.getThreadId(), c.getReactionCounts());
		}
	}
}
