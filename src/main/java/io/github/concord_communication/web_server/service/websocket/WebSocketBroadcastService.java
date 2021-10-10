package io.github.concord_communication.web_server.service.websocket;

import org.springframework.stereotype.Service;
import reactor.core.publisher.FluxSink;

import java.util.function.Consumer;

@Service
public class WebSocketBroadcastService implements Consumer<FluxSink<Object>> {
	private FluxSink<Object> sink;

	@Override
	public void accept(FluxSink<Object> sink) {
		if (this.sink != null) {
			this.sink.complete();
		}
		this.sink = sink;
	}

	public void send(Object obj) {
		if (this.sink != null) {
			this.sink.next(obj);
		}
	}
}
