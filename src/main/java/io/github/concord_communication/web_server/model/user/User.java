package io.github.concord_communication.web_server.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Document
@Getter
public class User implements UserDetails {
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
}
