package io.github.concord_communication.web_server.api.dto;

import io.github.concord_communication.web_server.model.Channel;

public record ChannelResponse (
		long id,
		String name,
		String description,
		long createdAt,
		long createdByUserId
) {
	public ChannelResponse(Channel channel) {
		this(channel.getId(), channel.getName(), channel.getDescription(), channel.getCreatedAt(), channel.getCreatedByUserId());
	}
}
