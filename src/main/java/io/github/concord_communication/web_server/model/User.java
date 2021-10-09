package io.github.concord_communication.web_server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document
@Getter
public class User {
	@Id
	private UUID id;
	private String username;

	@JsonIgnore
	private String passwordHash;

	public User(String username, String passwordHash) {
		this.id = UUID.randomUUID();
		this.username = username;
		this.passwordHash = passwordHash;
	}
}
