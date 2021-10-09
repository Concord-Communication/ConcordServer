package io.github.concord_communication.web_server.service;

import io.github.concord_communication.web_server.api.dto.UserRegistrationPayload;
import io.github.concord_communication.web_server.dao.UserRepository;
import io.github.concord_communication.web_server.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public Mono<User> registerNewUser(Mono<UserRegistrationPayload> payload) {
		return payload.flatMap(p -> this.userRepository.findByUsername(p.username())
				.flatMap(existingUser -> Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is taken.")))
				.switchIfEmpty(this.userRepository.save(new User(p.username(), this.passwordEncoder.encode(p.password()))))
				.cast(User.class)
		);
	}
}
