package io.github.concord_communication.web_server.api.user;

import io.github.concord_communication.web_server.api.user.dto.ProfileResponse;
import io.github.concord_communication.web_server.api.user.dto.UserRegistrationPayload;
import io.github.concord_communication.web_server.api.user.dto.UserResponse;
import io.github.concord_communication.web_server.dao.UserRepository;
import io.github.concord_communication.web_server.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UsersController {
	private final UserRepository userRepository;
	private final UserService userService;

	@GetMapping
	public Flux<UserResponse> getUsers() {
		return this.userRepository.findAll().map(UserResponse::new);
	}

	@PostMapping
	public Mono<UserResponse> registerNewUser(@RequestBody Mono<UserRegistrationPayload> payload) {
		return this.userService.registerNewUser(payload).map(UserResponse::new);
	}

	@GetMapping(path = "/{userId}")
	public Mono<UserResponse> getUser(@PathVariable long userId) {
		return this.userService.getUser(userId);
	}

	@GetMapping(path = "/{userId}/profile")
	public Mono<ProfileResponse> getProfile(@PathVariable long userId) {
		return this.userService.getProfile(userId);
	}
}
