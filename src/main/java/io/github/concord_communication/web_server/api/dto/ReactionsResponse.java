package io.github.concord_communication.web_server.api.dto;

import java.util.Map;
import java.util.Set;

public record ReactionsResponse (
		Map<String, Set<Long>> reactingUsers,
		Map<String, Integer> reactions
) {}
