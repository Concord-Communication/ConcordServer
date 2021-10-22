package io.github.concord_communication.web_server.api.user.dto;

public record FullUserResponse(
		long id,
		String username,
		ProfileResponse profile,
		StatusResponse status
) {}
