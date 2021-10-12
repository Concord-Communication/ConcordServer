package io.github.concord_communication.web_server.service.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.github.concord_communication.web_server.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;

import static io.github.concord_communication.web_server.util.JsonUtils.JSON;

/**
 * General handler that manages the setup for incoming websocket connections
 * from clients. This handler first checks the client's token (given as part of
 * the websocket URI), and if the token is valid, sets up the global broadcast
 * service and a new individual broadcast service to send messages to this new
 * client, and registers handlers for managing any messages the client sends.
 */
@Slf4j
public class ClientSocketHandler implements WebSocketHandler {
	private static final ObjectWriter writer = JSON.writer();
	private static final ObjectReader reader = JSON.reader();

	private final ClientBroadcastManager broadcastManager;
	private final ClientMessageHandler messageHandler;

	private final Flux<Object> globalMessageFlux;

	public ClientSocketHandler(ClientBroadcastManager broadcastManager, ClientMessageHandler messageHandler) {
		this.broadcastManager = broadcastManager;
		this.messageHandler = messageHandler;
		this.globalMessageFlux = broadcastManager.createGlobalMessageFlux();
	}

	@Override
	public Mono<Void> handle(WebSocketSession session) {
		return session.getHandshakeInfo().getPrincipal().flatMap(p -> {
			var user = (User) p;
			log.info("User {} has joined via websocket.", user.getUsername());
			return this.prepareClientCommunication(session, user);
		});
	}

	/**
	 * Prepares an authenticated client's websocket connection for operations,
	 * by forwarding all global messages to the client's session, creating a new
	 * flux for user-specific messages and forwarding these to the client's
	 * session, and by mapping any received messages to our {@link ClientMessageHandler}.
	 * @param session The client's websocket session.
	 * @param user The user that connected.
	 * @return A mono that completes once the client has disconnected.
	 */
	private Mono<Void> prepareClientCommunication(WebSocketSession session, User user) {
		var userMessageFlux = this.broadcastManager.createUserMessageFlux(user.getId());
		return Mono.when(
				// Continuously send messages from the global message flux.
				session.send(this.globalMessageFlux.map(o -> serialize(o, session))),
				// Continuously send messages from the user's personal message flux.
				session.send(userMessageFlux.map(o -> serialize(o, session))),
				// Handle any messages the user sends.
				session.receive().flatMap(ClientSocketHandler::deserialize).flatMap(j -> this.messageHandler.handle(user, j)),
				Mono.fromRunnable(() -> {
					log.info("Sending init messages.");
					this.broadcastManager.send(user.getId(), "Hello");
					this.broadcastManager.sendToAll("Everyone, please welcome " + user.getUsername());
				})
		).thenEmpty(s -> log.info("User {} has disconnected.", user.getUsername()));
	}

	private static WebSocketMessage serialize(Object obj, WebSocketSession session) {
		try {
			return session.textMessage(writer.writeValueAsString(obj));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	private static Mono<JsonNode> deserialize(WebSocketMessage msg) {
		return Mono.fromCallable(() -> {
			try {
				return reader.readTree(msg.getPayloadAsText());
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		});
	}
}
