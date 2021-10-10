package io.github.concord_communication.web_server.config;

import de.mkammerer.snowflakeid.SnowflakeIdGenerator;
import io.github.concord_communication.web_server.service.websocket.ClientSocketHandler;
import io.github.concord_communication.web_server.service.websocket.WebSocketAuthenticationStrategy;
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

@Configuration
@EnableWebFlux
@RequiredArgsConstructor
public class WebConfig implements WebFluxConfigurer {
	private final ClientSocketHandler clientSocketHandler;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedOrigins("*");
	}

	@Override
	public void configurePathMatching(PathMatchConfigurer configurer) {
		configurer.addPathPrefix("/api", HandlerTypePredicate.forAnnotation(RestController.class));
	}

	@Bean
	public HandlerMapping webSocketHandlerMapping() {
		var mapping = new SimpleUrlHandlerMapping(Map.of(
				"/client", this.clientSocketHandler
		));
		mapping.setOrder(1);
		return mapping;
	}

	@Bean
	public WebSocketService webSocketService() {
		return new HandshakeWebSocketService(new WebSocketAuthenticationStrategy());
	}

	@Bean
	public SnowflakeIdGenerator snowflakeIdGenerator() {
		return SnowflakeIdGenerator.createDefault(1);
	}
}
