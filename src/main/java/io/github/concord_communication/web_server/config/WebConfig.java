package io.github.concord_communication.web_server.config;

import de.mkammerer.snowflakeid.SnowflakeIdGenerator;
import io.github.concord_communication.web_server.api.ClientSocketHandler;
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

import java.util.Map;

@Configuration
@EnableWebFlux
public class WebConfig implements WebFluxConfigurer {
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
				"/client",
				new ClientSocketHandler()
		));
		mapping.setOrder(1);
		return mapping;
	}

	@Bean
	public SnowflakeIdGenerator snowflakeIdGenerator() {
		return SnowflakeIdGenerator.createDefault(1);
	}
}
