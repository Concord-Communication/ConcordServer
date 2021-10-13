package io.github.concord_communication.web_server.service.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.concord_communication.web_server.model.User;
import io.github.concord_communication.web_server.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static io.github.concord_communication.web_server.util.JsonUtils.JSON;

/**
 * This service is responsible for parsing and handling incoming client messages
 * that were sent via websocket.
 */
@Service
@RequiredArgsConstructor
public class ClientMessageHandler {
	private final ClientBroadcastManager broadcastManager;
	private final ChatService chatService;

	public Mono<Void> handle(User user, JsonNode msg) {
		String type = msg.get("type").asText();
		if (type.equals("heartbeat")) {
			this.broadcastManager.send(user.getId(), JSON.createObjectNode().put("type", "heartbeat"));
		} else if (type.equals("chat")) {
			return this.chatService.sendChatFromWebsocket(
					msg.get("channelId").asLong(),
					null,
					msg.get("content").asText(),
					user
			);
		}
		return Mono.empty();
	}
}
