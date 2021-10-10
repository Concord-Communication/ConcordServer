package io.github.concord_communication.web_server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static java.time.LocalDateTime.now;

public class ClientSocketHandler implements WebSocketHandler {
	private static final ObjectMapper json = new ObjectMapper();

	private Flux<String> eventFlux = Flux.generate(sink -> {
		String s = now().toString();
		sink.next(s);
	});

	private Flux<String> intervalFlux = Flux.interval(Duration.ofMillis(1000L))
			.zipWith(eventFlux, (time, event) -> event);

	/**
	 * Invoked when a new WebSocket connection is established, and allows
	 * handling of the session.
	 *
	 * <p>See the class-level doc and the reference manual for more details and
	 * examples of how to handle the session.
	 *
	 * @param session the session to handle
	 * @return indicates when application handling of the session is complete,
	 * which should reflect the completion of the inbound message stream
	 * (i.e. connection closing) and possibly the completion of the outbound
	 * message stream and the writing of messages
	 */
	@Override
	public Mono<Void> handle(WebSocketSession session) {
		return session.send(intervalFlux
				.map(session::textMessage))
				.and(session.receive()
						.map(WebSocketMessage::getPayloadAsText).log());
	}
}
