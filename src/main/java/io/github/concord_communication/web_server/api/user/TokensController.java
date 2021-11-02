package io.github.concord_communication.web_server.api.user;

import io.github.concord_communication.web_server.api.user.dto.TokenResponse;
import io.github.concord_communication.web_server.api.user.dto.UsernamePasswordPayload;
import io.github.concord_communication.web_server.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/tokens")
@RequiredArgsConstructor
public class TokensController {
	private final ReactiveAuthenticationManager authenticationManager;
	private final TokenService tokenService;

	@PostMapping
	public Mono<TokenResponse> authenticate(@RequestBody Mono<UsernamePasswordPayload> payload) {
		return payload
				.flatMap(login -> this.authenticationManager.authenticate(
						new UsernamePasswordAuthenticationToken(login.username(), login.password())
				)
				.onErrorMap(t -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid credentials.", t))
				.flatMap(this.tokenService::createToken));
	}

	@GetMapping("/recycle")
	public Mono<TokenResponse> getNewToken(Authentication auth) {
		return tokenService.createToken(auth);
	}
}
