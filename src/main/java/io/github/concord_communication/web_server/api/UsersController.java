package io.github.concord_communication.web_server.api;

import io.github.concord_communication.web_server.api.dto.UserRegistrationPayload;
import io.github.concord_communication.web_server.dao.UserRepository;
import io.github.concord_communication.web_server.model.User;
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
	public Flux<User> getUsers() {
		return this.userRepository.findAll();
	}

	@PostMapping
	public Mono<User> registerNewUser(@RequestBody Mono<UserRegistrationPayload> payload) {
		return this.userService.registerNewUser(payload);
	}
}
