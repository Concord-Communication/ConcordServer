package io.github.concord_communication.web_server.dao;

import io.github.concord_communication.web_server.model.user.UserProfile;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepository extends ReactiveMongoRepository<UserProfile, Long> {
}
