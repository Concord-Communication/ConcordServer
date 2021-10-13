package io.github.concord_communication.web_server.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Represents a chat message sent in a channel.
 */
@Document
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Chat {
	@Id
	private long id;

	/**
	 * The id of the user who sent the message.
	 */
	@Indexed
	private long authorId;

	/**
	 * The id of the channel that the message was sent in.
	 */
	@Indexed
	private long channelId;

	/**
	 * The id of the thread which this chat message was sent in. Chat messages
	 * that weren't sent in a thread will have this set to null.
	 */
	@Indexed
	private Long threadId;

	/**
	 * The timestamp of when the message was created.
	 */
	@Indexed
	private long createdAt;

	/**
	 * The textual content of the message.
	 */
	@TextIndexed
	private String content;
}
