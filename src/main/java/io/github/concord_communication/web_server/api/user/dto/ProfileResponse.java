package io.github.concord_communication.web_server.api.user.dto;

public record ProfileResponse(
		long userId,
		long createdAt,
		String nickname,
		String bio,
		String avatarUrl
) {}
