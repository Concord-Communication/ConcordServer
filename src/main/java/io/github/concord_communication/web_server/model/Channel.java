package io.github.concord_communication.web_server.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * A top-level channel that allows users to communicate with each other in a
 * variety of ways, as specified by the channel's set of capabilities.
 * TODO: Implement channel capabilities.
 */
@Document
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Channel {
	@Id
	private long id;

	/**
	 * The name of the channel.
	 */
	@Indexed
	private String name;

	/**
	 * A description of the channel. This may be null.
	 */
	private String description;

	/**
	 * The time at which the channel was created.
	 */
	private long createdAt;

	/**
	 * The user who created the channel.
	 */
	private long createdByUserId;
}
