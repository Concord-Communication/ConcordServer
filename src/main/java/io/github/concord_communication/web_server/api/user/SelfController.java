package io.github.concord_communication.web_server.api.user;

import io.github.concord_communication.web_server.api.user.dto.ProfileResponse;
import io.github.concord_communication.web_server.api.user.dto.UserResponse;
import io.github.concord_communication.web_server.model.user.User;
import io.github.concord_communication.web_server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/self")
@RequiredArgsConstructor
public class SelfController {
	private final UserService userService;

	@GetMapping
	public Mono<UserResponse> getSelf(@AuthenticationPrincipal User user) {
		return Mono.just(new UserResponse(user));
	}

	@GetMapping(path = "/profile")
	public Mono<ProfileResponse> getProfile(@AuthenticationPrincipal User user) {
		return userService.getProfile(user.getId());
	}
}
