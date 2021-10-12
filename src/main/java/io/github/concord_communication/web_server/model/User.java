package io.github.concord_communication.web_server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.security.auth.Subject;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;

@Document
@Getter
public class User implements UserDetails, Principal {
	@Id
	private long id;

	@Indexed(unique = true)
	private String username;

	@JsonIgnore
	private String passwordHash;

	public User(long id, String username, String passwordHash) {
		this.id = id;
		this.username = username;
		this.passwordHash = passwordHash;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return new ArrayList<>(0);
	}

	@Override
	public String getPassword() {
		return this.getPasswordHash();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	/**
	 * Returns the name of this principal.
	 *
	 * @return the name of this principal.
	 */
	@Override
	public String getName() {
		return this.username;
	}

	/**
	 * Returns true if the specified subject is implied by this principal.
	 *
	 * @param subject the {@code Subject}
	 * @return true if {@code subject} is non-null and is
	 * implied by this principal, or false otherwise.
	 * @implSpec The default implementation of this method returns true if
	 * {@code subject} is non-null and contains at least one principal that
	 * is equal to this principal.
	 *
	 * <p>Subclasses may override this with a different implementation, if
	 * necessary.
	 * @since 1.8
	 */
	@Override
	public boolean implies(Subject subject) {
		return Principal.super.implies(subject);
	}
}
