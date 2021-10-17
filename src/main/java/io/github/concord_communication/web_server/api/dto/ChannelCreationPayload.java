package io.github.concord_communication.web_server.api.dto;

import java.util.Set;

public record ChannelCreationPayload(
		String name,
		String description,
		Long parentChannelId,
		Integer ordinality,
		Set<String> capabilities
) {}
