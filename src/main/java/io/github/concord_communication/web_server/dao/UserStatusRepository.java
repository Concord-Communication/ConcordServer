package io.github.concord_communication.web_server.dao;

import io.github.concord_communication.web_server.model.user.UserStatus;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStatusRepository extends ReactiveMongoRepository<UserStatus, Long> {
}
