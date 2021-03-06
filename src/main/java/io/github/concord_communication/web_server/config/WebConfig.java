package io.github.concord_communication.web_server.config;

import io.github.concord_communication.web_server.service.TokenService;
import io.github.concord_communication.web_server.service.UserService;
import io.github.concord_communication.web_server.service.websocket.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.PathMatchConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.server.WebSocketService;
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService;

import java.util.Map;

/**
 * Web configuration for this application.
 */
@Configuration
@EnableWebFlux
@RequiredArgsConstructor
public class WebConfig implements WebFluxConfigurer {
	private final TokenService tokenService;
	private final ClientBroadcastManager clientBroadcastManager;
	private final ClientMessageHandler clientMessageHandler;
	private final UserService userService;
	private final MessageSerializer messageSerializer;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedOrigins("*")
				.allowedMethods("*")
				.allowedHeaders("*");
	}

	@Override
	public void configurePathMatching(PathMatchConfigurer configurer) {
		configurer.addPathPrefix("/api", HandlerTypePredicate.forAnnotation(RestController.class));
	}

	@Override
	public WebSocketService getWebSocketService() {
		return new HandshakeWebSocketService(new WebSocketAuthenticationStrategy(this.tokenService));
	}

	@Bean
	public HandlerMapping webSocketHandlerMapping() {
		var mapping = new SimpleUrlHandlerMapping(Map.of(
				"/client", new ClientSocketHandler(this.clientBroadcastManager, this.clientMessageHandler, this.userService, this.messageSerializer)
		));
		mapping.setOrder(1);
		return mapping;
	}
}
