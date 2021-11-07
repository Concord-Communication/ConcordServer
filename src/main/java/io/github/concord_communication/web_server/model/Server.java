package io.github.concord_communication.web_server.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Single document that holds basic information about the server itself. There
 * should only be one instance of this in the entire application.
 */
@Document
@NoArgsConstructor
@Getter
public class Server {
	/**
	 * The server's id is always fixed at 0, since there is always only one
	 * instance of it.
	 */
	@Id
	private long id = 0L;

	/**
	 * The name of the server. This should be relatively short and easy to share.
	 */
	private String name;

	/**
	 * The description of this server.
	 */
	private String description;

	/**
	 * An image id for this server's icon, if any.
	 */
	private Long iconId;

	/**
	 * The id of the channel that users should be directed to first, upon
	 * joining the server for the first time.
	 */
	@Setter
	private Long defaultChannelId;

	public Server(String name, String description, Long iconId) {
		this.name = name;
		this.description = description;
		this.iconId = iconId;
		this.defaultChannelId = null;
	}
}
