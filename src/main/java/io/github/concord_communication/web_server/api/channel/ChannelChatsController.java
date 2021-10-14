package io.github.concord_communication.web_server.api.channel;

import io.github.concord_communication.web_server.api.chat.ChatController;
import io.github.concord_communication.web_server.api.dto.ChatPayload;
import io.github.concord_communication.web_server.api.dto.ChatResponse;
import io.github.concord_communication.web_server.model.user.User;
import io.github.concord_communication.web_server.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Controller for a channel's collection of chats. This is used for sending new
 * chats and getting chats from this channel. For interacting with individual
 * chats, use the {@link ChatController}.
 */
@RestController
@RequestMapping(path = "/channels/{channelId}/chats")
@RequiredArgsConstructor
public class ChannelChatsController {
	private final ChatService chatService;

	@GetMapping(path = "/latest")
	public Flux<ChatResponse> getLatestChats(
			@PathVariable long channelId,
			@RequestParam(required = false, defaultValue = "50") int size
	) {
		return this.chatService.getLatest(channelId, size);
	}

	@PostMapping
	public Mono<ChatResponse> sendChat(
			@PathVariable long channelId,
			@RequestBody Mono<ChatPayload> payload,
			@AuthenticationPrincipal User user
	) {
		return this.chatService.sendChat(channelId, payload, user);
	}
}
