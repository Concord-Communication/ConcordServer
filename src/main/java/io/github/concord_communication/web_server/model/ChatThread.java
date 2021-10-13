package io.github.concord_communication.web_server.model;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Represents a thread of chat messages, which is a "sub" channel that a user
 * spawns from a single source chat message.
 */
@Document
@Getter
public class ChatThread {
	@Id
	private long id;

	/**
	 * The id of the chat message that this thread started from.
	 */
	@Indexed(unique = true)
	private long sourceChatId;

	/**
	 * The id of the user who created this thread.
	 */
	@Indexed
	private long creatorId;

	/**
	 * The time at which this thread was created.
	 */
	@Indexed
	private long createdAt;

	/**
	 * The name of this thread.
	 */
	@TextIndexed(weight = 2.0f)
	private String name;

	public ChatThread(long id, Chat source, String name) {
		this.id = id;
		this.sourceChatId = source.getId();
		this.name = name;
	}
}
