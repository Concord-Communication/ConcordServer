package io.github.concord_communication.web_server.model.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

/**
 * Detailed information about a user's presence on the server, including some
 * customization properties that allow the user to have a unique appearance, and
 * rights information that's used to determine the user's access to different
 * features of the server.
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

	/**
	 * A small bit of text about the user.
	 */
	@Setter
	private String bio;

	/**
	 * The id of the avatar image that this user's profile has.
	 */
	@Setter
	private Long avatarId;

	/**
	 * The id of the {@link io.github.concord_communication.web_server.model.user.rights.Rights}
	 * object that holds this user's personal, global rights.
	 */
	private long rightsId;

	/**
	 * The set of ids that defines the roles that this user has.
	 */
	private Set<Long> roleIds;

	public UserProfile(User user, long rightsId) {
		this.userId = user.getId();
		this.createdAt = System.currentTimeMillis();
		this.nickname = user.getUsername();
		this.bio = null;
		this.avatarId = null;
		this.rightsId = rightsId;
		this.roleIds = new HashSet<>();
	}
}
