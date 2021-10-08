package io.github.concord_communication.web_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import reactor.core.publisher.Flux;

import java.util.Map;

@SpringBootApplication
public class WebServerApplication {

	public static void main(String[] args) {
		System.setProperty("spring.profiles.active", "wsserver");
		SpringApplication.run(WebServerApplication.class, args);
	}

	@Bean
	SimpleUrlHandlerMapping testMapping() {
		return new SimpleUrlHandlerMapping(Map.of("/ws/greetings", greetingsHandler()), 10);
	}

	@Bean
	WebSocketHandler greetingsHandler() {
		return session -> {
			Flux<WebSocketMessage> out = session.receive()
					.map(WebSocketMessage::getPayloadAsText)
					.flatMap(s -> Flux.just("Hello " + s).map(session::textMessage));
			return session.send(out);
		};
	}
}
