package io.github.concord_communication.web_server.service;

import de.mkammerer.snowflakeid.SnowflakeIdGenerator;
import io.github.concord_communication.web_server.api.user.dto.ProfileResponse;
import io.github.concord_communication.web_server.api.user.dto.UserRegistrationPayload;
import io.github.concord_communication.web_server.api.user.dto.UserResponse;
import io.github.concord_communication.web_server.dao.UserProfileRepository;
import io.github.concord_communication.web_server.dao.UserRepository;
import io.github.concord_communication.web_server.model.user.User;
import io.github.concord_communication.web_server.model.user.UserProfile;
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
	private final UserProfileRepository profileRepository;
	private final PasswordEncoder passwordEncoder;
	private final SnowflakeIdGenerator idGenerator;

	@Transactional
	public Mono<User> registerNewUser(Mono<UserRegistrationPayload> payload) {
		return payload.flatMap(p -> this.userRepository.findByUsername(p.username())
				.flatMap(existingUser -> Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is taken.")))
				.switchIfEmpty(
					this.userRepository.save(new User(
							idGenerator.next(),
							p.username(),
							this.passwordEncoder.encode(p.password())
					))
					.flatMap(user -> this.profileRepository.save(new UserProfile(user)).thenReturn(user))
				)
				.cast(User.class)
		);
	}

	public Mono<UserResponse> getUser(long userId) {
		return this.userRepository.findById(userId)
				.flatMap(user -> Mono.just(new UserResponse(user)))
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.")));
	}

	public Mono<ProfileResponse> getProfile(long userId) {
		return profileRepository.findById(userId)
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.")))
				.map(profile -> new ProfileResponse(
						profile.getUserId(),
						profile.getCreatedAt(),
						profile.getNickname(),
						profile.getBio(),
						profile.getAvatarId() == null ? null : "/files/" + profile.getAvatarId()
				));
	}
}
