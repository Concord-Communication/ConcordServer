package io.github.concord_communication.web_server.api.dto;

import io.github.concord_communication.web_server.model.Chat;

import java.util.Map;

public record ChatResponse (
		long id,
		long createdAt,
		long authorId,
		long channelId,
		Long threadId,
		String content,
		boolean edited,
		Map<String, Integer> reactions
) {
	public ChatResponse(Chat chat) {
		this(
				chat.getId(),
				chat.getCreatedAt(),
				chat.getAuthorId(),
				chat.getChannelId(),
				chat.getThreadId(),
				chat.getContent(),
				chat.isEdited(),
				chat.getReactionCounts()
		);
	}
}
