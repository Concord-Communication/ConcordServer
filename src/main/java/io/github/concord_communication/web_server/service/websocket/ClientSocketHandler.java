package io.github.concord_communication.web_server.service.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.github.concord_communication.web_server.model.User;
import io.github.concord_communication.web_server.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * General handler that manages the setup for incoming websocket connections
 * from clients. This handler first checks the client's token (given as part of
 * the websocket URI), and if the token is valid, sets up the global broadcast
 * service and a new individual broadcast service to send messages to this new
 * client, and registers handlers for managing any messages the client sends.
 */
@Component
@Slf4j
public class ClientSocketHandler implements WebSocketHandler {
	private static final ObjectMapper json = new ObjectMapper();
	private static final ObjectWriter writer = json.writer();
	private final Map<Long, WebSocketBroadcastService> userBroadcastServices = new ConcurrentHashMap<>();
	private final Flux<Object> globalMessageFlux;

	private final TokenService tokenService;
	private final WebSocketBroadcastService globalBroadcastService;

	public ClientSocketHandler(WebSocketBroadcastService broadcastService, TokenService tokenService) {
		this.tokenService = tokenService;
		this.globalBroadcastService = broadcastService;
		this.globalMessageFlux = Flux.create(broadcastService);
	}

	@Override
	public Mono<Void> handle(WebSocketSession session) {
		var auth = getAuth(session);
		if (auth == null) return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or missing token."));
		var user = (User) auth.getPrincipal();
		log.info("User {} has joined via websocket", user.getUsername());
		var userBroadcastService = new WebSocketBroadcastService();
		var individualMessageFlux = Flux.create(userBroadcastService);
		this.userBroadcastServices.put(user.getId(), userBroadcastService);
		return Mono.when(
				// Continuously send messages from the global message flux.
				session.send(this.globalMessageFlux.map(o -> serialize(o, session))),
				// Continuously send messages from the user's personal message flux.
				session.send(individualMessageFlux.map(o -> serialize(o, session))),
				// Handle any messages the user sends.
				session.receive().map(WebSocketMessage::getPayloadAsText).log().then(),
				// After a few moments, send a "welcome" message.
				Mono.delay(Duration.ofSeconds(5)).flatMap(unused -> {
					userBroadcastService.send("Hello!");
					return Mono.empty();
				})
		);
	}

	private WebSocketMessage serialize(Object obj, WebSocketSession session) {
		try {
			return session.textMessage(writer.writeValueAsString(obj));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	private Authentication getAuth(WebSocketSession session) {
		var uri = session.getHandshakeInfo().getUri().toString();
		int idx = uri.indexOf("?token=");
		if (idx == -1) return null;
		return this.tokenService.getAuthentication(uri.substring(idx + 7));
	}
}
