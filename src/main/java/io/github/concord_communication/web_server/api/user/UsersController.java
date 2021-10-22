package io.github.concord_communication.web_server.api.user;

import io.github.concord_communication.web_server.api.user.dto.FullUserResponse;
import io.github.concord_communication.web_server.api.user.dto.UserRegistrationPayload;
import io.github.concord_communication.web_server.api.user.dto.UserResponse;
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
	private final UserService userService;

	@GetMapping
	public Flux<FullUserResponse> getUsers() {
		return this.userService.getUsers();
	}

	/**
	 * Endpoint for registering a new user account for this server.
	 * @param payload The user's registration data.
	 * @return A registration response.
	 */
	@PostMapping(path = "/register")
	public Mono<UserResponse> registerNewUser(@RequestBody Mono<UserRegistrationPayload> payload) {
		return this.userService.registerNewUser(payload).map(UserResponse::new);
	}

	@GetMapping(path = "/{userId}")
	public Mono<FullUserResponse> getUser(@PathVariable long userId) {
		return this.userService.getUser(userId);
	}
}
