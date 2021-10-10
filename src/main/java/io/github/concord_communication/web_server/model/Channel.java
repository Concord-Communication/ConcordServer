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
public class Channel {
	@Id
	private long id;

	@Indexed
	private String name;

	private String description;

	private long createdAt;

	private long createdByUserId;
}
