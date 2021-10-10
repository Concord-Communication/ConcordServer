package io.github.concord_communication.web_server.dao;

import io.github.concord_communication.web_server.model.Channel;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelRepository extends ReactiveMongoRepository<Channel, Long> {
}
