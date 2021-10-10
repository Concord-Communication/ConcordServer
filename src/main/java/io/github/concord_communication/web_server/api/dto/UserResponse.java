package io.github.concord_communication.web_server.api.dto;

import io.github.concord_communication.web_server.model.User;

public record UserResponse(
		long id,
		String username
) {
	public UserResponse(User user) {
		this(
				user.getId(),
				user.getUsername()
		);
	}
}
