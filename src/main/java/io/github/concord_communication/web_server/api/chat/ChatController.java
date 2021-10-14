package io.github.concord_communication.web_server.api.chat;

import io.github.concord_communication.web_server.api.dto.ChatEditPayload;
import io.github.concord_communication.web_server.api.dto.ChatResponse;
import io.github.concord_communication.web_server.model.User;
import io.github.concord_communication.web_server.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

	@PatchMapping
	public Mono<ChatResponse> editChat(
			@PathVariable long chatId,
			@RequestBody Mono<ChatEditPayload> payload,
			@AuthenticationPrincipal User user
	) {
		return this.chatService.editChat(chatId, payload, user);
	}

	@DeleteMapping
	public Mono<Void> removeChat(@PathVariable long chatId) {
		return this.chatService.removeChat(chatId);
	}
}
