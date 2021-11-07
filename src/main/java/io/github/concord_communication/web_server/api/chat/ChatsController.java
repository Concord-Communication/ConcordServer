package io.github.concord_communication.web_server.api.chat;

import io.github.concord_communication.web_server.api.dto.ChatResponse;
import io.github.concord_communication.web_server.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * Controller for interacting with all chats.
 */
@RestController
@RequestMapping(path = "/chats")
@RequiredArgsConstructor
public class ChatsController {
	private final ChatService chatService;

	/**
	 * The main endpoint for searching chats.
	 * @param page The page of results to show.
	 * @param size The size of the page.
	 * @param authorId The id of the author to find messages by.
	 * @param channelId The id of the channel to find messages in.
	 * @param threadId The id of a thread to find messages from. If set to -1,
	 *                 only messages sent in the main channel (NOT in a thread)
	 *                 will be returned.
	 * @param query A textual query to search messages for, based on a full-text
	 *              search of their content.
	 * @return A page of chat results.
	 */
	@GetMapping
	public Flux<ChatResponse> searchChats(
			@RequestParam(required = false, defaultValue = "0") int page,
			@RequestParam(required = false, defaultValue = "50") int size,
			@RequestParam(required = false) Long authorId,
			@RequestParam(required = false) Long channelId,
			@RequestParam(required = false) Long threadId,
			@RequestParam(required = false) Long before,
			@RequestParam(required = false) Long after,
			@RequestParam(required = false) String query
	) {
		return this.chatService.searchChats(page, size, authorId, channelId, threadId, before, after, query);
	}
}
