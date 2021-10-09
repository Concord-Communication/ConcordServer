package io.github.concord_communication.web_server.service;

import io.github.concord_communication.web_server.dao.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class UserDetailsService implements ReactiveUserDetailsService {
	private final UserRepository userRepository;

	@Override
	public Mono<UserDetails> findByUsername(String username) {
		return this.userRepository.findByUsername(username)
				.map(user -> User.withUsername(username)
						.password(user.getPasswordHash())
						.authorities(new ArrayList<>())
						.build());
	}
}
