package io.github.concord_communication.web_server.service.websocket;

import io.github.concord_communication.web_server.api.dto.ChatPayload;
import io.github.concord_communication.web_server.model.websocket.ChatMessages;
import io.github.concord_communication.web_server.model.websocket.ClientMessageEvent;
import io.github.concord_communication.web_server.model.websocket.Heartbeat;
import io.github.concord_communication.web_server.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * This service is responsible for parsing and handling incoming client messages
 * that were sent via websocket.
 */
@Service
@RequiredArgsConstructor
public class ClientMessageHandler {
	private final ClientBroadcastManager broadcastManager;
	private final ChatService chatService;

	public Mono<Void> handle(ClientMessageEvent event) {
		switch (event.type()) {
			case "heartbeat" -> {
				broadcastManager.send(event.user().getId(), new Heartbeat(System.currentTimeMillis()));
				return Mono.empty();
			}
			case "chat_written" -> {
				ChatMessages.Written chat = event.getMessage();
				return chatService.sendChatFromWebsocket(chat.channelId(), new ChatPayload(chat.sentAt(), chat.threadId(), chat.content()), event.user());
			}
			case "chat_typing" -> {
				ChatMessages.Typing typing = event.getMessage();
				broadcastManager.sendToAll(new ChatMessages.Typing(event.user().getId(), typing.channelId(), typing.threadId(), typing.sentAt()));
				return Mono.empty();
			}
		}
		return Mono.empty();
	}




}
