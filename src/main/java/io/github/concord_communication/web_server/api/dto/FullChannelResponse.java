package io.github.concord_communication.web_server.api.dto;

import java.util.List;
import java.util.Set;

public record FullChannelResponse(
		long id,
		int ordinality,
		String name,
		String description,
		Set<String> capabilities,
		long createdAt,
		long createdByUserId,
		List<FullChannelResponse> children
) {}
