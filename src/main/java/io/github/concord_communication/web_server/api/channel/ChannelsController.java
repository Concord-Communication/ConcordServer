package io.github.concord_communication.web_server.api.channel;

import io.github.concord_communication.web_server.api.dto.ChannelCreationPayload;
import io.github.concord_communication.web_server.api.dto.ChannelResponse;
import io.github.concord_communication.web_server.api.dto.FullChannelResponse;
import io.github.concord_communication.web_server.model.user.User;
import io.github.concord_communication.web_server.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/channels")
@RequiredArgsConstructor
public class ChannelsController {
	private final ChannelService channelService;

	@GetMapping
	public Flux<FullChannelResponse> getAllChannels() {
		return this.channelService.getAllRecursive();
	}

	@GetMapping(path = "/{channelId}")
	public Mono<ChannelResponse> getChannel(@PathVariable long channelId) {
		return this.channelService.get(channelId);
	}

	@PostMapping
	public Mono<ChannelResponse> createChannel(
			@RequestBody Mono<ChannelCreationPayload> payload,
			@AuthenticationPrincipal User user
	) {
		return this.channelService.createChannel(payload, user);
	}

	@DeleteMapping(path = "/{channelId}")
	public Mono<Void> removeChannel(@PathVariable long channelId) {
		return this.channelService.removeChannel(channelId);
	}
}
