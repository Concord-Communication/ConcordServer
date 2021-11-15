package io.github.concord_communication.web_server.service.websocket;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A singleton manager that's responsible for keeping track of various broadcast
 * services that are used to send messages to users via websocket.
 */
@Service
public class ClientBroadcastManager {
	private final WebSocketBroadcastService globalBroadcastService = new WebSocketBroadcastService();
	private final Map<Long, WebSocketBroadcastService> userBroadcastServices = new ConcurrentHashMap<>();

	/**
	 * Creates a flux which can be used to send messages to all clients.
	 * @return The flux to send messages to.
	 */
	public Flux<Object> createGlobalMessageFlux() {
		return Flux.create(this.globalBroadcastService);
	}

	/**
	 * Creates a flux which can be used to send messages to a single user.
	 * @param userId The id of the user to create a flux for.
	 * @return The flux to send messages to.
	 */
	public Flux<Object> createUserMessageFlux(long userId) {
		var userService = new WebSocketBroadcastService();
		this.userBroadcastServices.put(userId, userService);
		return Flux.create(userService);
	}

	/**
	 * Sends a message to all clients via a global broadcast service.
	 * @param msg The message to send.
	 */
	public void sendToAll(Object msg) {
		this.globalBroadcastService.send(msg);
	}

	/**
	 * Sends a message to a user.
	 * @param userId The id of the user to send the message to.
	 * @param msg The message to send.
	 */
	public void send(long userId, Object msg) {
		var service = userBroadcastServices.get(userId);
		if (service != null) {
			service.send(msg);
		}
	}

	/**
	 * Closes the connection to a user.
	 * @param userId The id of the user to close the connection to.
	 */
	public void closeConnection(Long userId) {
		var service = userBroadcastServices.get(userId);
		if (service != null) {
			service.complete();
		}
	}
}
