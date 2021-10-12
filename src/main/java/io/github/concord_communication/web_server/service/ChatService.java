package io.github.concord_communication.web_server.service;

import de.mkammerer.snowflakeid.SnowflakeIdGenerator;
import io.github.concord_communication.web_server.api.dto.ChatPayload;
import io.github.concord_communication.web_server.api.dto.ChatResponse;
import io.github.concord_communication.web_server.dao.ChannelRepository;
import io.github.concord_communication.web_server.dao.ChatRepository;
import io.github.concord_communication.web_server.model.Chat;
import io.github.concord_communication.web_server.model.User;
import io.github.concord_communication.web_server.service.websocket.ClientBroadcastManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {
	private final ChatRepository chatRepository;
	private final ChannelRepository channelRepository;
	private final SnowflakeIdGenerator idGenerator;
	private final ClientBroadcastManager broadcastManager;

	public Mono<ChatResponse> getChat(long chatId) {
		return this.chatRepository.findById(chatId)
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat not found.")))
				.map(ChatResponse::new);
	}

	@Transactional
	public Mono<ChatResponse> sendChat(long channelId, Mono<ChatPayload> payload, User user) {
		return payload.flatMap(data -> channelRepository.findById(channelId)
				.flatMap(channel -> chatRepository.save(new Chat(
						idGenerator.next(),
						user.getId(),
						channel.getId(),
						null,
						data.sentAt() == null ? System.currentTimeMillis() : data.sentAt(),
						data.content()
				)))
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown channel id.")))).map(chat -> {
			this.broadcastManager.sendToAll(chat);
			return new ChatResponse(chat);
		});
	}

	@Transactional
	public Mono<Void> sendChatFromWebsocket(long channelId, Long threadId, String content, User user) {
		return this.channelRepository.findById(channelId)
				.flatMap(channel -> chatRepository.save(new Chat(
						idGenerator.next(),
						user.getId(),
						channelId,
						threadId,
						System.currentTimeMillis(),
						content
				)))
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown channel id.")))
				.flatMap(chat -> {
					this.broadcastManager.sendToAll(chat);
					return Mono.empty();
				});
	}

	public Flux<ChatResponse> getLatest(long channelId, int size) {
		return this.chatRepository.findAllByChannelId(channelId, PageRequest.of(0, size, Sort.by(Sort.Order.desc("createdAt"))))
				.map(ChatResponse::new);
	}

	public Mono<Void> removeChat(long chatId) {
		return this.chatRepository.deleteById(chatId);
	}
}
