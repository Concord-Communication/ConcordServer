package io.github.concord_communication.web_server.dao;

import io.github.concord_communication.web_server.model.Server;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServerRepository extends ReactiveMongoRepository<Server, Long> {

}
