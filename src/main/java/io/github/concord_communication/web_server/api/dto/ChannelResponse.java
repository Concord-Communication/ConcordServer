package io.github.concord_communication.web_server.api.dto;

import io.github.concord_communication.web_server.model.Channel;

import java.util.Set;

public record ChannelResponse (
		long id,
		Long parentChannelId,
		int ordinality,
		String name,
		String description,
		Set<String> capabilities,
		long createdAt,
		long createdByUserId
) {
	public ChannelResponse(Channel channel) {
		this(
				channel.getId(),
				channel.getParentChannelId(),
				channel.getOrdinality(),
				channel.getName(),
				channel.getDescription(),
				channel.getCapabilities(),
				channel.getCreatedAt(),
				channel.getCreatedByUserId()
		);
	}
}
