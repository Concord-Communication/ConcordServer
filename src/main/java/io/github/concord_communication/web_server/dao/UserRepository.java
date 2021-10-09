package io.github.concord_communication.web_server.dao;

import io.github.concord_communication.web_server.model.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface UserRepository extends ReactiveMongoRepository<User, UUID> {
	Mono<User> findByUsername(String username);
}
