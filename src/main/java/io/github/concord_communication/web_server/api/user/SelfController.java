package io.github.concord_communication.web_server.api.user;

import io.github.concord_communication.web_server.api.user.dto.FullUserResponse;
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
	public Mono<FullUserResponse> getSelf(@AuthenticationPrincipal User user) {
		return this.userService.getUser(user.getId());
	}
}
