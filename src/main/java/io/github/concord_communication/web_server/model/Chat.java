package io.github.concord_communication.web_server.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Chat {
	@Id
	private long id;

	@Indexed
	private long authorId;

	@Indexed
	private long channelId;

	@Indexed
	private Long threadId;

	@Indexed
	private long createdAt;

	@Indexed
	private String content;
}
