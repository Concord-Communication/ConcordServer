package io.github.concord_communication.web_server.dao;

import io.github.concord_communication.web_server.model.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveMongoRepository<User, Long> {
	Mono<User> findByUsername(String username);
}
