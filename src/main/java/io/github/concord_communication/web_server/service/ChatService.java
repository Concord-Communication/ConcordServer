package io.github.concord_communication.web_server.service;

import de.mkammerer.snowflakeid.SnowflakeIdGenerator;
import io.github.concord_communication.web_server.api.dto.ChatPayload;
import io.github.concord_communication.web_server.api.dto.ChatResponse;
import io.github.concord_communication.web_server.dao.ChannelRepository;
import io.github.concord_communication.web_server.dao.ChatRepository;
import io.github.concord_communication.web_server.model.Chat;
import io.github.concord_communication.web_server.model.User;
import io.github.concord_communication.web_server.service.websocket.WebSocketBroadcastService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ChatService {
	private final ChatRepository chatRepository;
	private final ChannelRepository channelRepository;
	private final SnowflakeIdGenerator idGenerator;
	private final WebSocketBroadcastService broadcastService;

	public Mono<ChatResponse> getChat(long chatId) {
		return this.chatRepository.findById(chatId)
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat not found.")))
				.map(ChatResponse::new);
	}

	@Transactional
	public Mono<ChatResponse> sendChat(long channelId, Mono<ChatPayload> payload, User user) {
		return payload.flatMap(data -> {
			return channelRepository.findById(channelId)
					.flatMap(channel -> chatRepository.save(new Chat(
							idGenerator.next(),
							user.getId(),
							channel.getId(),
							null,
							data.sentAt() == null ? System.currentTimeMillis() : data.sentAt(),
							data.content()
					)))
					.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown channel id.")));
		}).map(chat -> {
			this.broadcastService.send(chat);
			return new ChatResponse(chat);
		});
	}

	public Flux<ChatResponse> getLatest(long channelId) {
		return this.chatRepository.findAllByChannelId(channelId, PageRequest.of(0, 50))
				.map(ChatResponse::new);
	}

	public Mono<Void> removeChat(long chatId) {
		return this.chatRepository.deleteById(chatId);
	}
}
