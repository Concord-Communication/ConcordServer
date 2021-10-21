package io.github.concord_communication.web_server.service.websocket;

import reactor.core.publisher.FluxSink;

import java.util.function.Consumer;

/**
 * A simple service that is able to send objects into a flux sink (basically a
 * non-blocking queue) that it has consumed.
 */
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

	public void complete() {
		this.sink.complete();
	}
}
