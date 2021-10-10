package io.github.concord_communication.web_server.config;

import io.github.concord_communication.web_server.security.JwtAuthenticationFilter;
import io.github.concord_communication.web_server.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, TokenService tokenService) {
		return http.authorizeExchange()
				.pathMatchers("/client").permitAll()
				.pathMatchers("/api/tokens/**", "/api/users").permitAll()
				.anyExchange().authenticated()
				.and()
				.addFilterAt(new JwtAuthenticationFilter(tokenService), SecurityWebFiltersOrder.HTTP_BASIC)
				.csrf().disable()
				.httpBasic().disable()
				.logout().disable()
				.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(10);
	}

	@Bean
	public ReactiveAuthenticationManager reactiveAuthenticationManager(ReactiveUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
		var authManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
		authManager.setPasswordEncoder(passwordEncoder);
		return authManager;
	}
}
