package io.github.concord_communication.web_server.service.websocket;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ClientBroadcastManager {
	private final WebSocketBroadcastService globalBroadcastService = new WebSocketBroadcastService();
	private final Map<Long, WebSocketBroadcastService> userBroadcastServices = new ConcurrentHashMap<>();

	public Flux<Object> createGlobalMessageFlux() {
		return Flux.create(this.globalBroadcastService);
	}

	public Flux<Object> createUserMessageFlux(long userId) {
		var userService = new WebSocketBroadcastService();
		this.userBroadcastServices.put(userId, userService);
		return Flux.create(userService);
	}

	public void sendToAll(Object msg) {
		this.globalBroadcastService.send(msg);
	}

	public void send(long userId, Object msg) {
		var service = userBroadcastServices.get(userId);
		if (service != null) {
			service.send(msg);
		}
	}
}
