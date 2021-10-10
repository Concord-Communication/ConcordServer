package io.github.concord_communication.web_server.service.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.server.reactive.AbstractServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.reactive.socket.HandshakeInfo;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.adapter.NettyWebSocketSessionSupport;
import org.springframework.web.reactive.socket.adapter.ReactorNettyWebSocketSession;
import org.springframework.web.reactive.socket.server.RequestUpgradeStrategy;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerResponse;

import java.util.function.Supplier;

@Slf4j
public class WebSocketAuthenticationStrategy implements RequestUpgradeStrategy {
	/**
	 * Upgrade to a WebSocket session and handle it with the given handler.
	 *
	 * @param exchange             the current exchange
	 * @param webSocketHandler     handler for the WebSocket session
	 * @param subProtocol          the selected sub-protocol got the handler
	 * @param handshakeInfoFactory factory to create HandshakeInfo for the WebSocket session
	 * @return completion {@code Mono<Void>} to indicate the outcome of the
	 * WebSocket session handling.
	 * @since 5.1
	 */
	@Override
	public Mono<Void> upgrade(ServerWebExchange exchange, WebSocketHandler webSocketHandler, String subProtocol, Supplier<HandshakeInfo> handshakeInfoFactory) {
		log.info("Handling websocket upgrade {}.", handshakeInfoFactory.get().getUri());
		var handshakeInfo = handshakeInfoFactory.get();
		var response = exchange.getResponse();
		var reactorResponse = getNativeResponse(response);
		return reactorResponse.sendWebsocket((websocketInbound, websocketOutbound) -> {
			System.out.println("sending websocket...");
			ReactorNettyWebSocketSession session = new ReactorNettyWebSocketSession(
					websocketInbound,
					websocketOutbound,
					handshakeInfo,
					(NettyDataBufferFactory) response.bufferFactory(),
					NettyWebSocketSessionSupport.DEFAULT_FRAME_MAX_SIZE
			);
			return webSocketHandler.handle(session);
		});
	}

	private static HttpServerResponse getNativeResponse(ServerHttpResponse response) {
		if (response instanceof AbstractServerHttpResponse) {
			return ((AbstractServerHttpResponse) response).getNativeResponse();
		} else if (response instanceof ServerHttpResponseDecorator) {
			return getNativeResponse(((ServerHttpResponseDecorator) response).getDelegate());
		} else {
			throw new IllegalArgumentException("Couldn't find native response in " + response.getClass().getName());
		}
	}
}
