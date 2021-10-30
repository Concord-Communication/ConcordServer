package io.github.concord_communication.web_server.api;

import io.github.concord_communication.web_server.api.dto.ServerResponse;
import io.github.concord_communication.web_server.api.dto.ServerUpdatePayload;
import io.github.concord_communication.web_server.service.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/server")
@RequiredArgsConstructor
public class ServerController {
	private final ServerService serverService;

	@GetMapping
	public Mono<ServerResponse> getServerData() {
		return serverService.getServerData();
	}

	@PatchMapping
	public Mono<ServerResponse> updateServerData(Mono<ServerUpdatePayload> payload) {
		return Mono.error(new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED));
	}

	@PostMapping(path = "/icon", consumes = {"image/png", "image/jpeg", "image/webp", "image/gif"})
	public Mono<ServerResponse> updateServerIcon(ServerHttpRequest request) {
		return Mono.error(new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED));
	}
}
