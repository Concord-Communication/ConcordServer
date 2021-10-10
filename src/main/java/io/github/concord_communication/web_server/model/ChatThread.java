package io.github.concord_communication.web_server.model;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
public class ChatThread {
	@Id
	private long id;

	@Indexed(unique = true)
	private long sourceChatId;

	private long creatorId;

	@Indexed
	private long createdAt;

	@Indexed
	private String name;

	public ChatThread(long id, Chat source, String name) {
		this.id = id;
		this.sourceChatId = source.getId();
		this.name = name;
	}
}
