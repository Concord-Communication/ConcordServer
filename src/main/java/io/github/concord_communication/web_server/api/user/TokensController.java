package io.github.concord_communication.web_server.api.user;

import io.github.concord_communication.web_server.api.user.dto.UsernamePasswordPayload;
import io.github.concord_communication.web_server.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping(path = "/tokens")
@RequiredArgsConstructor
public class TokensController {
	private final ReactiveAuthenticationManager authenticationManager;
	private final TokenService tokenService;

	@PostMapping
	public Mono<ResponseEntity<?>> authenticate(@RequestBody Mono<UsernamePasswordPayload> payload) {
		return payload
				.flatMap(login -> this.authenticationManager.authenticate(
						new UsernamePasswordAuthenticationToken(login.username(), login.password())
				)
				.onErrorMap(t -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid credentials.", t))
				.map(this.tokenService::createToken))
				.map(token -> ResponseEntity.ok(Map.of("token", token)));
	}
}
