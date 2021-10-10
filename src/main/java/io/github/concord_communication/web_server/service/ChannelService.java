package io.github.concord_communication.web_server.service;

import de.mkammerer.snowflakeid.SnowflakeIdGenerator;
import io.github.concord_communication.web_server.api.dto.ChannelCreationPayload;
import io.github.concord_communication.web_server.api.dto.ChannelResponse;
import io.github.concord_communication.web_server.dao.ChannelRepository;
import io.github.concord_communication.web_server.dao.ChatRepository;
import io.github.concord_communication.web_server.model.Channel;
import io.github.concord_communication.web_server.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ChannelService {
	private final ChannelRepository channelRepository;
	private final ChatRepository chatRepository;
	private final SnowflakeIdGenerator idGenerator;

	public Flux<ChannelResponse> getAll() {
		return channelRepository.findAll(Sort.by(Sort.Order.asc("name")))
				.map(ChannelResponse::new);
	}

	public Mono<ChannelResponse> get(long id) {
		return channelRepository.findById(id)
				.map(ChannelResponse::new)
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Channel not found.")));
	}

	@Transactional
	public Mono<ChannelResponse> createChannel(Mono<ChannelCreationPayload> payload, User user) {
		return payload.flatMap(data -> {
			var channel = new Channel(idGenerator.next(), data.name(), data.description(), System.currentTimeMillis(), user.getId());
			return this.channelRepository.save(channel);
		}).map(ChannelResponse::new);
	}

	@Transactional
	public Mono<Void> removeChannel(long channelId) {
		return this.chatRepository.deleteAllByChannelId(channelId)
				.then(this.channelRepository.deleteById(channelId));
	}
}
