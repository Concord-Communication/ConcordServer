package io.github.concord_communication.web_server.model.user.rights;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a set of rights that a user or a role can have (or for more fine-
 * grained control, per-channel rights for users and/or roles).
 * <p>
 *     At the core of the rights system is the <em>value</em> which each set of
 *     rights may contribute to determining if a user has permission to do
 *     something. Depending on the context, a user may have many sets of rights
 *     that describe what they can or cannot do, based on their roles, their
 *     personal rights, and what channel they're doing something in.
 * </p>
 * <p>
 *     Each set of rights assigns an integer value to zero or more unique rights
 *     as a way of indicating how strongly this set of rights affects a user's
 *     ability to do something. A <em>negative</em> value indicates that this
 *     set of rights discourages the user from having the associated right, and
 *     a <em>positive</em> value encourages the user to have the associated
 *     right. A value of zero or the absence of a value indicates no preference.
 * </p>
 * <p>
 *     To determine whether a user has a right, we find all sets of {@link Rights}
 *     that apply to the user, and take the sum of all values for that right
 *     from every set. If the sum is positive, the user has the right. If the
 *     sum is negative, the user does not have the right. If the value is zero,
 *     the user may or may not have the right, depending on if the server admin
 *     decides to allow <code>defaultRightsAccess</code> in the server's
 *     configuration.
 * </p>
 */
@Document
@Getter
public class Rights {
	@Id
	private long id;

	/**
	 * If set to true, users with these rights have access to everything, which
	 * overrides any other personal or channel restrictions.
	 */
	@Setter
	private boolean admin = false;

	/**
	 * The set of values for this set of rights. For each right identified by
	 * its unique name, this map may contain an integer value that helps to
	 * determine if users with these rights should be permitted to have the
	 * associated right.
	 */
	private Map<String, Integer> values;

	public Rights(long id) {
		this.id = id;
		this.values = new HashMap<>();
	}
}
