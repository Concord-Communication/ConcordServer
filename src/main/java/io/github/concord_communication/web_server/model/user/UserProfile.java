package io.github.concord_communication.web_server.model.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Information about a user's custom profile.
 */
@Document
@NoArgsConstructor
@Getter
public class UserProfile {
	@Id
	private long userId;

	private long createdAt;

	@Indexed
	@Setter
	private String nickname;

	@Setter
	private String bio;

	@Setter
	private Long avatarId;

	public UserProfile(User user) {
		this.userId = user.getId();
		this.createdAt = System.currentTimeMillis();
		this.nickname = user.getUsername();
		this.bio = "";
		this.avatarId = null;
	}
}
