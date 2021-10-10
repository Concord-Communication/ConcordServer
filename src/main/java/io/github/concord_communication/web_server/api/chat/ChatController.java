package io.github.concord_communication.web_server.api.chat;

import io.github.concord_communication.web_server.api.dto.ChatResponse;
import io.github.concord_communication.web_server.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * Controller for interacting with a single chat.
 */
@RestController
@RequestMapping(path = "/chats/{chatId}")
@RequiredArgsConstructor
public class ChatController {
	private final ChatService chatService;

	@GetMapping
	public Mono<ChatResponse> getChat(@PathVariable long chatId) {
		return this.chatService.getChat(chatId);
	}

	@DeleteMapping
	public Mono<Void> removeChat(@PathVariable long chatId) {
		return this.chatService.removeChat(chatId);
	}
}
