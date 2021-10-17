package io.github.concord_communication.web_server.service;

import de.mkammerer.snowflakeid.SnowflakeIdGenerator;
import io.github.concord_communication.web_server.api.dto.ChannelCreationPayload;
import io.github.concord_communication.web_server.api.dto.ChannelResponse;
import io.github.concord_communication.web_server.api.dto.FullChannelResponse;
import io.github.concord_communication.web_server.dao.ChannelRepository;
import io.github.concord_communication.web_server.dao.ChatRepository;
import io.github.concord_communication.web_server.model.Channel;
import io.github.concord_communication.web_server.model.user.User;
import io.github.concord_communication.web_server.model.websocket.ChannelMessages;
import io.github.concord_communication.web_server.service.websocket.ClientBroadcastManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ChannelService {
	private final ChannelRepository channelRepository;
	private final ChatRepository chatRepository;
	private final SnowflakeIdGenerator idGenerator;
	private final ClientBroadcastManager broadcastManager;

	public Flux<FullChannelResponse> getAllRecursive() {
		return channelRepository.findAllByParentChannelIdOrderByOrdinality(null)
				.expandDeep(channel -> channelRepository.findAllByParentChannelIdOrderByOrdinality(channel.getId()))
				.collectList()
				.flatMapMany(channels -> Flux.fromIterable(buildRecursiveChannelResponse(null, channels)));
	}

	public List<FullChannelResponse> buildRecursiveChannelResponse(Long parent, List<Channel> channels) {
		List<FullChannelResponse> responses = new ArrayList<>();
		while (!channels.isEmpty()) {
			Channel c = channels.get(0);
			if (!Objects.equals(c.getParentChannelId(), parent)) return responses;
			channels.remove(0);
			var children = buildRecursiveChannelResponse(c.getId(), channels);
			responses.add(new FullChannelResponse(
					c.getId(),
					c.getOrdinality(),
					c.getName(),
					c.getDescription(),
					c.getCapabilities(),
					c.getCreatedAt(),
					c.getCreatedByUserId(),
					children
			));
		}
		return responses;
	}

	public Mono<ChannelResponse> get(long id) {
		return channelRepository.findById(id)
				.map(ChannelResponse::new)
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Channel not found.")));
	}

	@Transactional
	public Mono<ChannelResponse> createChannel(Mono<ChannelCreationPayload> payload, User user) {
		return payload.flatMap(data -> {
			var channel = new Channel(
					idGenerator.next(),
					data.parentChannelId(),
					data.ordinality() == null ? 0 : data.ordinality(),
					data.name(),
					data.description(),
					data.capabilities(),
					System.currentTimeMillis(),
					user.getId()
			);
			return channelRepository.save(channel);
		})
				.flatMap(channel ->  normalizeOrdinality(channel.getParentChannelId())
						.filter(c -> c.getId() == channel.getId()).next()
						.map(loadedChannel -> {
							var resp = new ChannelResponse(loadedChannel);
							broadcastManager.sendToAll(new ChannelMessages.Created(resp));
							return resp;
						})
				);
	}

	@Transactional
	public Mono<Void> removeChannel(long channelId) {
		return this.chatRepository.deleteAllByChannelId(channelId)
				.then(this.channelRepository.deleteById(channelId))
				.doOnSuccess(unused -> broadcastManager.sendToAll(new ChannelMessages.Deleted(channelId)));
	}

	@Transactional
	public Mono<ChannelResponse> updateChannel(long channelId) {
		return Mono.empty();
	}

	private Flux<Channel> normalizeOrdinality(Long parentId) {
		return channelRepository.findAllByParentChannelIdOrderByOrdinality(parentId).buffer()
				.flatMap(channels -> {
					for (int i = 0; i < channels.size(); i++) {
						channels.get(i).setOrdinality(i);
					}
					return channelRepository.saveAll(channels);
				});
	}
}
