package io.github.concord_communication.web_server.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A top-level channel that allows users to communicate with each other in a
 * variety of ways, as specified by the channel's set of capabilities.
 */
@Document
@Getter
@NoArgsConstructor
public class Channel {
	@Id
	private long id;

	/**
	 * The id of a parent channel that this one belongs to.
	 */
	@Indexed
	private Long parentChannelId;

	/**
	 * The ordinality, or order of this channel. This determines the order in
	 * which the channel should appear when several channels from the same
	 * parent are shown in a list. Channels with the same ordinality should be
	 * ordered by their name.
	 */
	@Indexed
	@Setter
	private int ordinality;

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
	 * The set of capabilities this channel offers.
	 */
	private Set<String> capabilities;

	/**
	 * The time at which the channel was created.
	 */
	private long createdAt;

	/**
	 * The user who created the channel.
	 */
	private long createdByUserId;

	/**
	 * A map that associates user ids with a specific set of rights that the
	 * user gets, only in the context of this channel or any child channels.
	 */
	private Map<Long, Long> userRightsIds;

	/**
	 * A map that associates role ids with a specific set of rights that all
	 * users of the role get, only in the context of this channel or any child
	 * channels.
	 */
	private Map<Long, Long> roleRightsIds;

	public Channel(long id, Long parentChannelId, int ordinality, String name, String description, Set<String> capabilities, long createdAt, long createdByUserId) {
		this.id = id;
		this.parentChannelId = parentChannelId;
		this.ordinality = ordinality;
		this.name = name;
		this.description = description;
		this.capabilities = capabilities;
		this.createdAt = createdAt;
		this.createdByUserId = createdByUserId;
		this.userRightsIds = new HashMap<>();
		this.roleRightsIds = new HashMap<>();
	}
}
