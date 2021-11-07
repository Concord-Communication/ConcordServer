package io.github.concord_communication.web_server.service;

import io.github.concord_communication.web_server.api.dto.ServerResponse;
import io.github.concord_communication.web_server.dao.ServerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ServerService {
	private final ServerRepository serverRepository;

	@Transactional(readOnly = true)
	public Mono<ServerResponse> getServerData() {
		return this.serverRepository.findById(0L)
				.map(server -> new ServerResponse(server.getName(), server.getDescription(), server.getIconId(), server.getDefaultChannelId()));
	}
}
