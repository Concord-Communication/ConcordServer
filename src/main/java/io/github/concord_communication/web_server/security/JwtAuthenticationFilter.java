package io.github.concord_communication.web_server.security;

import io.github.concord_communication.web_server.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * A security filter that checks for the presence of a JWT authentication token
 * and if found, adds a user authentication instance to the current web exchange
 * security context.
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {
	private final TokenService tokenService;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		String token = this.tokenService.extractToken(exchange.getRequest());
		if (token != null) {
			var auth = this.tokenService.getAuthentication(token);
			if (auth != null) {
				return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
			}
		}
		return chain.filter(exchange);
	}
}
