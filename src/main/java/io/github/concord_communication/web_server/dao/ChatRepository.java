package io.github.concord_communication.web_server.dao;

import io.github.concord_communication.web_server.model.Chat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ChatRepository extends ReactiveMongoRepository<Chat, Long> {
	Mono<Chat> findByChannelIdAndId(long channelId, long id);
	Flux<Chat> findAllByChannelId(long channelId, Pageable pageable);
	Mono<Void> deleteAllByChannelId(long channelId);
}
