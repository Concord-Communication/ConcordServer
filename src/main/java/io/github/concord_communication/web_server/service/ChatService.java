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
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static io.github.concord_communication.web_server.util.JsonUtils.JSON;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {
	private final ChatRepository chatRepository;
	private final ChannelRepository channelRepository;
	private final SnowflakeIdGenerator idGenerator;
	private final ClientBroadcastManager broadcastManager;
	private final ReactiveMongoTemplate mongoTemplate;

	public Mono<ChatResponse> getChat(long chatId) {
		return this.chatRepository.findById(chatId)
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat not found.")))
				.map(ChatResponse::new);
	}

	@Transactional
	public Mono<ChatResponse> sendChat(long channelId, Mono<ChatPayload> payload, User user) {
		return payload.flatMap(data -> sendChat(channelId, data, user))
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown channel id.")))
				.map(ChatResponse::new);
	}

	@Transactional
	public Mono<Void> sendChatFromWebsocket(long channelId, Long threadId, String content, User user) {
		return sendChat(channelId, new ChatPayload(System.currentTimeMillis(), content), user)
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown channel id.")))
				.then();
	}

	private Mono<Chat> sendChat(long channelId, ChatPayload payload, User author) {
		return channelRepository.findById(channelId)
				.flatMap(channel -> chatRepository.save(new Chat(
						idGenerator.next(), author.getId(), channelId, null,
						payload.sentAt() == null ? System.currentTimeMillis() : payload.sentAt(),
						payload.content()
				)))
				.doOnSuccess(broadcastManager::sendToAll);
	}

	public Flux<ChatResponse> getLatest(long channelId, int size) {
		return this.chatRepository.findAllByChannelId(channelId, PageRequest.of(0, size, Sort.by(Sort.Order.desc("createdAt"))))
				.map(ChatResponse::new);
	}

	public Flux<ChatResponse> searchChats(int page, int size, Long authorId, Long channelId, Long threadId, String textQuery) {
		Query q = new Query();
		if (authorId != null) q.addCriteria(Criteria.where("authorId").is(authorId));
		if (channelId != null) q.addCriteria(Criteria.where("channelId").is(channelId));
		if (threadId != null) {
			Long targetValue = threadId == -1 ? null : threadId;
			q.addCriteria(Criteria.where("threadId").is(targetValue));
		}
		if (textQuery != null && !textQuery.isBlank()) {
			q.addCriteria(TextCriteria.forDefaultLanguage().matchingAny(textQuery));
		}
		q.with(PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt"))));
		return this.mongoTemplate.find(q, Chat.class).map(ChatResponse::new);
	}

	public Mono<Void> removeChat(long chatId) {
		return this.chatRepository.deleteById(chatId)
				.doOnSuccess(unused -> this.broadcastManager.sendToAll(JSON.createObjectNode()
						.put("type", "chat_deleted")
						.put("id", chatId)
				));
	}
}
