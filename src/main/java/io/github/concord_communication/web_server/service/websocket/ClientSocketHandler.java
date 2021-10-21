package io.github.concord_communication.web_server.service.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.concord_communication.web_server.model.user.User;
import io.github.concord_communication.web_server.model.websocket.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

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

	private final static Map<String, Class<?>> messageTypes = new HashMap<>();
	static {// Register all possible message types that clients can send.
		registerTypes(Heartbeat.class, ChatMessages.Written.class, ChatMessages.Typing.class, ChatMessages.Reaction.class);
	}

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
			Authentication auth = (Authentication) p;
			User user = (User) auth.getPrincipal();
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
		this.messageHandler.getHeartbeatMonitor().beginTracking(user.getId(), session);
		Mono<Void> globalMessageSending = session.send(this.globalMessageFlux.map(o -> serialize(o, session)));
		Mono<Void> personalMessageSending = session.send(Flux.merge(
				userMessageFlux,
				Flux.interval(Duration.ofSeconds(10)).flatMap(x -> sendHeartbeat(user.getId()))
		).map(o -> serialize(o, session)));
		Flux<Void> messageReceiving = session.receive().flatMap(msg -> deserialize(msg, user)).flatMap(this.messageHandler::handle);
		Mono<Void> initialMessageSending = Mono.fromRunnable(() -> {
			this.broadcastManager.sendToAll(new UserMessages.StatusUpdated(user.getId(), "online"));
		});
		// TODO: Figure out how to terminate message receiving when heartbeat timeout is reached.
		return Mono.when(globalMessageSending, personalMessageSending, messageReceiving, initialMessageSending)
				.doAfterTerminate(() -> {
					messageHandler.getHeartbeatMonitor().endTracking(user.getId());
					log.info("User {} has disconnected.", user.getUsername());
				});
	}

	private static WebSocketMessage serialize(Object obj, WebSocketSession session) {
		var messageType = obj.getClass().getAnnotation(MessageType.class);
		try {
			if (messageType == null) {
				return session.textMessage(writer.writeValueAsString(obj));
			} else {
				ObjectNode node = JSON.valueToTree(obj);
				node.put("type", messageType.value());
				return session.textMessage(node.toString());
			}
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	private static Mono<ClientMessageEvent> deserialize(WebSocketMessage msg, User user) {
		return Mono.fromCallable(() -> {
			JsonNode node = reader.readTree(msg.getPayloadAsText());
			var typeNode = node.get("type");
			if (typeNode == null || typeNode.isMissingNode() || !typeNode.isTextual()) {
				log.warn("Missing or non-textual message type received.");
				return null;
			}
			String type = typeNode.asText();
			var messageType = messageTypes.get(type);
			if (messageType == null) return null;
			try {
				return new ClientMessageEvent(user, type, reader.treeToValue(node, messageType));
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		});
	}

	private Mono<Object> sendHeartbeat(long userId) {
		log.info("Sending heartbeat to {}", userId);
		long now = System.currentTimeMillis();
		return Mono.just(new Heartbeat(now));
	}

	private static void registerTypes(Class<?>... types) {
		for (var type : types) {
			var annotation = type.getAnnotation(MessageType.class);
			if (annotation == null) throw new IllegalArgumentException("Invalid message type: " + type.getSimpleName());
			messageTypes.put(annotation.value(), type);
		}
	}
}
