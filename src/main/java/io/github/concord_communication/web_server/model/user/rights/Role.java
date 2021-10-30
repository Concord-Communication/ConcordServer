package io.github.concord_communication.web_server.model.user.rights;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * A role is an entity which contains its own set of rights, much like a user,
 * but many roles can be assigned to a user.
 */
@Document
@Getter
public class Role {
	@Id
	private long id;

	/**
	 * The name for this role.
	 */
	@Indexed(unique = true)
	private String name;

	/**
	 * The order in which this role should be shown, relative to others.
	 */
	private int ordinality;

	/**
	 * The id of the {@link Rights} object that this role has.
	 */
	private long rightsId;

	public Role(long id, String name, int ordinality, long rightsId) {
		this.id = id;
		this.name = name;
		this.ordinality = ordinality;
		this.rightsId = rightsId;
	}
}
