package io.github.concord_communication.web_server.service;

import io.github.concord_communication.web_server.dao.ChannelRepository;
import io.github.concord_communication.web_server.dao.RightsRepository;
import io.github.concord_communication.web_server.dao.RoleRepository;
import io.github.concord_communication.web_server.dao.UserProfileRepository;
import io.github.concord_communication.web_server.model.Channel;
import io.github.concord_communication.web_server.model.user.UserProfile;
import io.github.concord_communication.web_server.model.user.rights.Rights;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RightsService {
	private final UserProfileRepository userProfileRepository;
	private final RightsRepository rightsRepository;
	private final RoleRepository roleRepository;
	private final ChannelRepository channelRepository;
	private final ReactiveMongoTemplate mongoTemplate;

	/**
	 * Determines if the user has the specified right, possibly in the context
	 * of a specified channel. This uses a consensus algorithm where the user's
	 * rights are determined by a consensus of all the different sets of rights
	 * that are relevant in a particular context.
	 * @param userId The id of the user.
	 * @param channelId The id of the channel. This may be null.
	 * @param right The right to check.
	 * @return A mono that resolves to true if the user has the given right, or
	 * false otherwise.
	 */
	public Mono<Boolean> hasRight(long userId, Long channelId, String right) {
		var rightsFlux = getRights(userId, channelId);
		return rightsFlux.collectList()
				.map(rightsSets -> {
					int consensus = 0;
					for (var rights : rightsSets) {
						if (rights.isAdmin()) return true;
						Integer value = rights.getValues().get(right);
						if (value != null) consensus += value;
					}
					return consensus >= 0;
				});
	}

	public Flux<Rights> getRights(long userId, Long channelId) {
		Mono<UserProfile> userProfileMono = userProfileRepository.findById(userId);
		Flux<Rights> globalRights = userProfileMono
				.flatMapMany(profile -> {
					List<Mono<Rights>> rightsMonos = new ArrayList<>();
					rightsMonos.add(rightsRepository.findById(profile.getRightsId()));
					for (var roleId : profile.getRoleIds()) {
						rightsMonos.add(roleRepository.findById(roleId).flatMap(role -> rightsRepository.findById(role.getRightsId())));
					}
					return Flux.concat(rightsMonos);
				});
		if (channelId == null) return globalRights;
		Flux<Channel> relevantChannels = channelRepository.findById(channelId)
				.expandDeep(channel -> channel.getParentChannelId() == null ? Mono.empty() : channelRepository.findById(channel.getParentChannelId()));
		Flux<Rights> channelRights = relevantChannels
				.flatMap(channel -> {
					List<Mono<Rights>> rightsMonos = new ArrayList<>();
					if (channel.getUserRightsIds().containsKey(userId)) {
						rightsMonos.add(rightsRepository.findById(channel.getUserRightsIds().get(userId)));
					}
					Flux<Rights> rightsFlux = Flux.concat(rightsMonos);
					Flux<Rights> channelRoleRightsFlux = userProfileMono.flatMapMany(profile -> {
						List<Mono<Rights>> rm = new ArrayList<>();
						for (var roleId : profile.getRoleIds()) {
							if (channel.getRoleRightsIds().containsKey(roleId)) {
								rm.add(rightsRepository.findById(channel.getRoleRightsIds().get(roleId)));
							}
						}
						return Flux.concat(rm);
					});
					return Flux.concat(rightsFlux, channelRoleRightsFlux);
				});
		return Flux.concat(globalRights, channelRights);
	}
}
