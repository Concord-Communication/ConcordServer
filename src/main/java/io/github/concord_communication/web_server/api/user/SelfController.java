package io.github.concord_communication.web_server.api.user;

import io.github.concord_communication.web_server.api.user.dto.FullUserResponse;
import io.github.concord_communication.web_server.api.user.dto.UpdatePasswordPayload;
import io.github.concord_communication.web_server.model.user.User;
import io.github.concord_communication.web_server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
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

	@PostMapping(path = "/avatar", consumes = {"image/png", "image/jpeg", "image/webp", "image/gif"})
	public Mono<FullUserResponse> updateAvatar(@AuthenticationPrincipal User user, ServerHttpRequest request) {
		return this.userService.updateAvatar(user, request);
	}

	@PostMapping(path = "/password")
	public Mono<Void> updatePassword(@AuthenticationPrincipal User user, @RequestBody Mono<UpdatePasswordPayload> payload) {
		return this.userService.updatePassword(user, payload);
	}
}
