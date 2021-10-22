package io.github.concord_communication.web_server.service;

import de.mkammerer.snowflakeid.SnowflakeIdGenerator;
import io.github.concord_communication.web_server.api.user.dto.FullUserResponse;
import io.github.concord_communication.web_server.api.user.dto.ProfileResponse;
import io.github.concord_communication.web_server.api.user.dto.StatusResponse;
import io.github.concord_communication.web_server.api.user.dto.UserRegistrationPayload;
import io.github.concord_communication.web_server.dao.UserProfileRepository;
import io.github.concord_communication.web_server.dao.UserRepository;
import io.github.concord_communication.web_server.dao.UserStatusRepository;
import io.github.concord_communication.web_server.model.user.User;
import io.github.concord_communication.web_server.model.user.UserProfile;
import io.github.concord_communication.web_server.model.user.UserStatus;
import io.github.concord_communication.web_server.model.websocket.UserMessages;
import io.github.concord_communication.web_server.service.websocket.ClientBroadcastManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
	private final UserRepository userRepository;
	private final UserProfileRepository profileRepository;
	private final UserStatusRepository statusRepository;
	private final PasswordEncoder passwordEncoder;
	private final SnowflakeIdGenerator idGenerator;
	private final ReactiveMongoTemplate mongoTemplate;

	private final ClientBroadcastManager broadcastManager;

	@Transactional(readOnly = true)
	public Flux<FullUserResponse> getUsers() {
		AggregationOperation profileLookup = Aggregation.lookup("userProfile", "_id", "_id", "profile");
		AggregationOperation statusLookup = Aggregation.lookup("userStatus", "_id", "_id", "status");
		Aggregation agr = Aggregation.newAggregation(profileLookup, statusLookup);
		var results = mongoTemplate.aggregate(agr, "user", Document.class);
		return results.map(this::buildFullUserResponse);
	}

	private FullUserResponse buildFullUserResponse(Document doc) {
		var profiles = doc.get("profile", List.class);
		ProfileResponse pr = null;
		if (!profiles.isEmpty()) {
			var p = (Document) profiles.get(0);
			pr = new ProfileResponse(p.getLong("createdAt"), p.getString("nickname"), p.getString("bio"), p.getLong("avatarId"));
		}
		var statuses = doc.get("status", List.class);
		StatusResponse sr = null;
		if (!statuses.isEmpty()) {
			var s = (Document) statuses.get(0);
			sr = new StatusResponse(s.getString("onlineStatus"));
		}
		return new FullUserResponse(doc.getLong("_id"), doc.getString("username"), pr, sr);
	}

	@Transactional
	public Mono<User> registerNewUser(Mono<UserRegistrationPayload> payload) {
		return payload.flatMap(p -> this.userRepository.findByUsername(p.username())
				.flatMap(existingUser -> Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is taken.")))
				.switchIfEmpty(
					this.userRepository.save(new User(
							idGenerator.next(),
							p.username(),
							this.passwordEncoder.encode(p.password())
					))
					.flatMap(user -> Mono.when(
							this.profileRepository.save(new UserProfile(user)),
							this.statusRepository.save(new UserStatus(user))
					).thenReturn(user).doOnSuccess(u -> broadcastManager.sendToAll(new UserMessages.Joined(u.getId()))))
				)
				.cast(User.class)
		);
	}

	public Mono<FullUserResponse> getUser(long userId) {
		return this.userRepository.findById(userId)
				.zipWith(this.profileRepository.findById(userId))
				.zipWith(this.statusRepository.findById(userId))
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.")))
				.map(objects -> {
					var user = objects.getT1().getT1();
					var profile = objects.getT1().getT2();
					var status = objects.getT2();
					return new FullUserResponse(user.getId(), user.getUsername(), new ProfileResponse(profile), new StatusResponse(status));
				});
	}

	public Mono<ProfileResponse> getProfile(long userId) {
		return profileRepository.findById(userId)
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.")))
				.map(profile -> new ProfileResponse(
						profile.getCreatedAt(),
						profile.getNickname(),
						profile.getBio(),
						profile.getAvatarId()
				));
	}

	@Transactional
	public Mono<Void> updateStatus(long userId, UserStatus.OnlineStatus newStatus) {
		return statusRepository.findById(userId)
				.flatMap(status -> {
					if (status.getOnlineStatus() != newStatus) {
						status.setOnlineStatus(newStatus);
						return statusRepository.save(status)
								.doOnSuccess(savedStatus -> {
									log.info("User {}'s status has been updated to {}.", savedStatus.getUserId(), savedStatus.getOnlineStatus().name());
									this.broadcastManager.sendToAll(new UserMessages.StatusUpdated(savedStatus.getUserId(), savedStatus.getOnlineStatus().name()));
								}).then();
					} else {
						return Mono.empty();
					}
				});
	}
}
