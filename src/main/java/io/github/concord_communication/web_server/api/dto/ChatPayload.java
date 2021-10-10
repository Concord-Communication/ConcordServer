package io.github.concord_communication.web_server.api.dto;

public record ChatPayload(
		Long sentAt,
		String content
) {
}
