package io.github.concord_communication.web_server.service.websocket;

import io.github.concord_communication.web_server.model.user.User;
import io.github.concord_communication.web_server.model.user.UserStatus;
import io.github.concord_communication.web_server.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Duration;

/**
 * General handler that manages the setup for incoming websocket connections
 * from clients. This handler first checks the client's token (given as part of
 * the websocket URI), and if the token is valid, sets up the global broadcast
 * service and a new individual broadcast service to send messages to this new
 * client, and registers handlers for managing any messages the client sends.
 */
@Slf4j
public class ClientSocketHandler implements WebSocketHandler {
	private final ClientBroadcastManager broadcastManager;
	private final ClientMessageHandler messageHandler;
	private final UserService userService;
	private final MessageSerializer messageSerializer;

	private final Flux<Object> globalMessageFlux;

	public ClientSocketHandler(ClientBroadcastManager broadcastManager, ClientMessageHandler messageHandler, UserService userService, MessageSerializer messageSerializer) {
		this.broadcastManager = broadcastManager;
		this.messageHandler = messageHandler;
		this.userService = userService;
		this.messageSerializer = messageSerializer;
		this.globalMessageFlux = broadcastManager.createGlobalMessageFlux();
	}

	@Override
	public Mono<Void> handle(WebSocketSession session) {
		return session.getHandshakeInfo().getPrincipal().flatMap(p -> {
			Authentication auth = (Authentication) p;
			User user = (User) auth.getPrincipal();
			log.info("User \"{}\" has joined via websocket.", user.getUsername());
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
				session.send(this.globalMessageFlux.map(o -> messageSerializer.serialize(o, session))),
				session.send(userMessageFlux.map(o -> messageSerializer.serialize(o, session))),
				session.receive().flatMap(msg -> messageSerializer.deserialize(msg, user)).flatMap(this.messageHandler::handle),
				session.closeStatus().then(Mono.error(new IOException("User closed the connection."))),
				Mono.delay(Duration.ofSeconds(1)).then(this.userService.updateStatus(user.getId(), UserStatus.OnlineStatus.ONLINE))
		).onErrorResume(throwable -> {// Handle the connection reset error that is thrown when the client disconnects.
			log.info("User \"{}\" has disconnected from the websocket: {}.", user.getUsername(), throwable.getMessage());
			return this.userService.updateStatus(user.getId(), UserStatus.OnlineStatus.OFFLINE);
		});
	}
}
