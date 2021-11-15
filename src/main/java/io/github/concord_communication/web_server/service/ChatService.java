package io.github.concord_communication.web_server.service;

import de.mkammerer.snowflakeid.SnowflakeIdGenerator;
import io.github.concord_communication.web_server.api.dto.*;
import io.github.concord_communication.web_server.dao.ChannelRepository;
import io.github.concord_communication.web_server.dao.ChatRepository;
import io.github.concord_communication.web_server.model.Chat;
import io.github.concord_communication.web_server.model.user.User;
import io.github.concord_communication.web_server.model.websocket.ChatMessages;
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

import java.util.HashSet;

/**
 * This service is responsible for handling updates and requests pertaining to
 * the set of chat messages in the server.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {
	private final ChatRepository chatRepository;
	private final ChannelRepository channelRepository;
	private final SnowflakeIdGenerator idGenerator;
	private final ClientBroadcastManager broadcastManager;
	private final ReactiveMongoTemplate mongoTemplate;

	/**
	 * Gets a single chat by its id.
	 * @param chatId The id of the chat to fetch.
	 * @return A mono that contains the chat response, or a 404 error.
	 */
	public Mono<ChatResponse> getChat(long chatId) {
		return this.chatRepository.findById(chatId)
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat not found.")))
				.map(ChatResponse::new);
	}

	/**
	 * Gets the latest chats from a channel.
	 * @param channelId The id of the channel to fetch chats from.
	 * @param size The number of chats to return.
	 * @return A page of chats.
	 */
	public Flux<ChatResponse> getLatest(long channelId, int size) {
		return this.chatRepository.findAllByChannelId(channelId, PageRequest.of(0, size, Sort.by(Sort.Order.desc("createdAt"))))
				.map(ChatResponse::new);
	}

	/**
	 * Searches through chats.
	 * @param page The page of results to show.
	 * @param size The size of results.
	 * @param authorId The id of the author to filter by.
	 * @param channelId The id of the channel to filter by.
	 * @param threadId The id of the thread to filter by.
	 * @param before A timestamp that filters to only chats before it.
	 * @param after A timestamp that filters to only chats after it.
	 * @param textQuery A full-text string to match messages against.
	 * @return A list of chats matching the criteria.
	 */
	public Flux<ChatResponse> searchChats(int page, int size, Long authorId, Long channelId, Long threadId, Long before, Long after, String textQuery) {
		Query q = new Query();
		if (authorId != null) q.addCriteria(Criteria.where("authorId").is(authorId));
		if (channelId != null) q.addCriteria(Criteria.where("channelId").is(channelId));
		if (threadId != null) {
			Long targetValue = threadId == -1 ? null : threadId;
			q.addCriteria(Criteria.where("threadId").is(targetValue));
		}
		if (before != null) q.addCriteria(Criteria.where("createdAt").lt(before));
		if (after != null) q.addCriteria(Criteria.where("createdAt").gt(after));
		if (textQuery != null && !textQuery.isBlank()) {
			q.addCriteria(TextCriteria.forDefaultLanguage().matchingAny(textQuery));
		}
		q.with(PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt"))));
		return this.mongoTemplate.find(q, Chat.class).map(ChatResponse::new);
	}

	/**
	 * Sends a new chat in a channel.
	 * @param channelId The id of the channel to send the chat to.
	 * @param payload The chat data.
	 * @param user The user who is sending the message.
	 * @return A chat response for the chat that was created, or a 400 bad
	 * request error if the chat couldn't be sent.
	 */
	@Transactional
	public Mono<ChatResponse> sendChat(long channelId, Mono<ChatPayload> payload, User user) {
		return payload.flatMap(data -> sendChat(channelId, data, user))
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown channel id.")))
				.map(ChatResponse::new);
	}

	/**
	 * Sends a chat in a new channel, from the context of a websocket.
	 * @param channelId The id of the channel to send the chat to.
	 * @param payload The chat data.
	 * @param user The user who is sending the message.
	 * @return A mono that's complete when the chat is sent.
	 */
	@Transactional
	public Mono<Void> sendChatFromWebsocket(long channelId, ChatPayload payload, User user) {
		return sendChat(channelId, payload, user)
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
				.doOnSuccess(chat -> broadcastManager.sendToAll(new ChatMessages.Sent(chat)));
	}

	/**
	 * Edits a user's chat message. When the message is edited, the server sends
	 * a message to all clients indicating that the message has been updated.
	 * @param chatId The id of the chat to edit.
	 * @param payloadMono The data about the edit.
	 * @param user The user who's editing their chat message.
	 * @return A response containing the updated chat.
	 */
	@Transactional
	public Mono<ChatResponse> editChat(long chatId, Mono<ChatEditPayload> payloadMono, User user) {
		return payloadMono.flatMap(payload -> chatRepository.findById(chatId)
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat not found.")))
				.filter(chat -> chat.getAuthorId() == user.getId())
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the author can edit a chat message.")))
				.flatMap(chat -> {
					chat.setContent(payload.content());
					chat.setEdited(true);
					return chatRepository.save(chat);
				})
				.doOnSuccess(chat -> broadcastManager.sendToAll(new ChatMessages.Edited(chat)))
				.map(ChatResponse::new));
	}

	/**
	 * Removes a chat. Sends a message to all connected clients that the chat
	 * has been removed.
	 * @param chatId The id of the chat to remove.
	 * @return A mono that completes when the chat is removed.
	 */
	public Mono<Void> removeChat(long chatId) {
		return this.chatRepository.findById(chatId)
				.flatMap(chat -> this.chatRepository.delete(chat)
						.doOnSuccess(unused -> this.broadcastManager.sendToAll(new ChatMessages.Deleted(chat))));
	}

	/**
	 * Gets the set of reactions for a chat. This includes the standard reaction
	 * information (number of votes per reaction), and additionally shows which
	 * user has given each reaction.
	 * @param chatId The id of the chat to fetch reactions for.
	 * @return The reaction data.
	 */
	public Mono<ReactionsResponse> getReactions(long chatId) {
		return chatRepository.findById(chatId)
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat not found.")))
				.map(chat -> new ReactionsResponse(chat.getReactions(), chat.getReactionCounts()));
	}

	/**
	 * Adds a reaction to a chat message. Sends a message to every connected
	 * client to inform them of this reaction.
	 * @param chatId The id of the chat to add a reaction to.
	 * @param payload The reaction data.
	 * @param user The user who's adding a reaction.
	 * @return A mono that completes when the reaction has been added.
	 */
	@Transactional
	public Mono<Void> addReaction(long chatId, Mono<ReactionPayload> payload, User user) {
		return payload.flatMap(data -> chatRepository.findById(chatId)
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat not found.")))
				.flatMap(chat -> {
					var reactionUserIds = chat.getReactions().computeIfAbsent(data.reaction(), r -> new HashSet<>());
					if (data.adding()) {
						reactionUserIds.add(user.getId());
					} else {
						reactionUserIds.remove(user.getId());
						if (reactionUserIds.isEmpty()) {
							chat.getReactions().remove(data.reaction());
						}
					}
					return chatRepository.save(chat)
							.doOnSuccess(c -> this.broadcastManager.sendToAll(new ChatMessages.ReactionsUpdated(c)));
				}).then()
		);
	}
}
