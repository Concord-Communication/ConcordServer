package io.github.concord_communication.web_server.api.user.dto;

import io.github.concord_communication.web_server.model.user.UserStatus;

public record StatusResponse(
		String onlineStatus
) {
	public StatusResponse(UserStatus status) {
		this(status.getOnlineStatus().name());
	}
}
