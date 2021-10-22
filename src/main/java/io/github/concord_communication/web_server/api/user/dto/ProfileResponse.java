package io.github.concord_communication.web_server.api.user.dto;

import io.github.concord_communication.web_server.model.user.UserProfile;

public record ProfileResponse(
		long createdAt,
		String nickname,
		String bio,
		Long avatarId
) {
	public ProfileResponse(UserProfile profile) {
		this(profile.getCreatedAt(), profile.getNickname(), profile.getBio(), profile.getAvatarId());
	}
}
