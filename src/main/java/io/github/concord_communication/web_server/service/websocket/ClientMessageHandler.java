package io.github.concord_communication.web_server.service.websocket;

import io.github.concord_communication.web_server.api.dto.ChatPayload;
import io.github.concord_communication.web_server.api.dto.ReactionPayload;
import io.github.concord_communication.web_server.model.websocket.ChatMessages;
import io.github.concord_communication.web_server.model.websocket.ClientMessageEvent;
import io.github.concord_communication.web_server.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * This service is responsible for parsing and handling incoming client messages
 * that were sent via websocket.
 */
@Service
@Slf4j
public class ClientMessageHandler {
	private final ClientBroadcastManager broadcastManager;
	private final ChatService chatService;

	public ClientMessageHandler(ClientBroadcastManager broadcastManager, ChatService chatService) {
		this.broadcastManager = broadcastManager;
		this.chatService = chatService;
	}

	/**
	 * Handles a client message that was received over the websocket.
	 * @param event The message event.
	 * @return A mono that completes when the message is handled.
	 */
	public Mono<Void> handle(ClientMessageEvent event) {
		switch (event.type()) {
			case "chat_written" -> {
				ChatMessages.Written chat = event.getMessage();
				return chatService.sendChatFromWebsocket(chat.channelId(), new ChatPayload(chat.sentAt(), chat.threadId(), chat.content()), event.user());
			}
			case "chat_typing" -> {
				ChatMessages.Typing typing = event.getMessage();
				broadcastManager.sendToAll(new ChatMessages.Typing(event.user().getId(), typing.channelId(), typing.threadId(), typing.sentAt()));
				return Mono.empty();
			}
			case "chat_reaction" -> {
				ChatMessages.Reaction reactionMsg = event.getMessage();
				var data = Mono.just(new ReactionPayload(reactionMsg.reaction(), reactionMsg.adding()));
				return this.chatService.addReaction(reactionMsg.chatId(), data, event.user());
			}
		}
		return Mono.empty();
	}
}
