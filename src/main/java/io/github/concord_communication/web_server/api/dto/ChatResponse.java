package io.github.concord_communication.web_server.api.dto;

import io.github.concord_communication.web_server.model.Chat;

public record ChatResponse (
		long id,
		long createdAt,
		long authorId,
		long channelId,
		Long threadId,
		String content,
		boolean edited
) {
	public ChatResponse(Chat chat) {
		this(
				chat.getId(),
				chat.getCreatedAt(),
				chat.getAuthorId(),
				chat.getChannelId(),
				chat.getThreadId(),
				chat.getContent(),
				chat.isEdited()
		);
	}
}
