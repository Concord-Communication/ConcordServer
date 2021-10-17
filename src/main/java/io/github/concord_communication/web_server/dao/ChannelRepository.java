package io.github.concord_communication.web_server.dao;

import io.github.concord_communication.web_server.model.Channel;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ChannelRepository extends ReactiveMongoRepository<Channel, Long> {
	Flux<Channel> findAllByParentChannelIdOrderByOrdinality(Long parentChannelId);
	Flux<Channel> findAllByParentChannelId(Long parentChannelId);
}
