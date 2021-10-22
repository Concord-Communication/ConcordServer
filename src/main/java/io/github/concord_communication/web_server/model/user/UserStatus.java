package io.github.concord_communication.web_server.model.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Information about a user's status. This tells if the user is online, what
 * they might be doing (if they choose to share that info), or what applications
 * or activities they're busy with.
 */
@Document
@NoArgsConstructor
@Getter
public class UserStatus {
	public enum OnlineStatus {
		OFFLINE,
		ONLINE,
		AWAY,
		DO_NOT_DISTURB
	}

	@Id
	private long userId;

	@Indexed
	@Setter
	private OnlineStatus onlineStatus;

	public UserStatus(User user) {
		this.userId = user.getId();
		this.onlineStatus = OnlineStatus.OFFLINE;
	}
}
