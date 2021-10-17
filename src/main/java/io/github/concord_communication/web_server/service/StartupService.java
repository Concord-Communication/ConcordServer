package io.github.concord_communication.web_server.service;

import io.github.concord_communication.web_server.api.dto.ChannelCreationPayload;
import io.github.concord_communication.web_server.api.user.dto.UserRegistrationPayload;
import io.github.concord_communication.web_server.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Set;

/**
 * This service is responsible for handling any jobs that need to be run when
 * the application starts.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StartupService implements ApplicationRunner {
	private final ReactiveMongoTemplate mongoTemplate;
	private final UserService userService;
	private final ChannelService channelService;

	@Override
	public void run(ApplicationArguments args) {
		if (args.getOptionNames().contains("reset")) {
			resetServer();
		}
	}

	/**
	 * Wipes all data from the server, and creates a default admin user.
	 */
	private void resetServer() {
		log.info("Resetting Concord server.");
		mongoTemplate.getCollectionNames()
				.flatMap(mongoTemplate::dropCollection)
				.then()
				.block();
		log.info("Dropped all collections.");
		var userData = new UserRegistrationPayload("admin", StringUtils.random(40));
		var adminUser = this.userService.registerNewUser(Mono.just(userData)).block();
		log.info("Registered default admin user \"{}\" with password \"{}\".", userData.username(), userData.password());
		var channelData = new ChannelCreationPayload("general", "The default chat channel", null, 0, Set.of("text"));
		var channel = this.channelService.createChannel(Mono.just(channelData), adminUser).block();
		log.info("Created default #{} channel.", channel.name());
	}
}
