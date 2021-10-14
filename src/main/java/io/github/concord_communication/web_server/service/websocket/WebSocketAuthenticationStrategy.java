package io.github.concord_communication.web_server.service.websocket;

import io.github.concord_communication.web_server.model.User;
import io.github.concord_communication.web_server.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.AbstractServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.security.core.Authentication;
import org.springframework.web.reactive.socket.HandshakeInfo;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.adapter.NettyWebSocketSessionSupport;
import org.springframework.web.reactive.socket.adapter.ReactorNettyWebSocketSession;
import org.springframework.web.reactive.socket.server.RequestUpgradeStrategy;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerResponse;

import java.util.function.Supplier;

/**
 * This request upgrade strategy is responsible for authenticating any incoming
 * websocket request before allowing it to proceed.
 */
@Slf4j
@RequiredArgsConstructor
public class WebSocketAuthenticationStrategy implements RequestUpgradeStrategy {
	private final TokenService tokenService;

	@Override
	public Mono<Void> upgrade(ServerWebExchange exchange, WebSocketHandler webSocketHandler, String subProtocol, Supplier<HandshakeInfo> handshakeInfoFactory) {
		var handshakeInfo = handshakeInfoFactory.get();
		var response = exchange.getResponse();
		var reactorResponse = getNativeResponse(response);
		Authentication auth = this.getAuth(handshakeInfo);
		if (auth == null) {
			return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or missing token."));
		}
		return reactorResponse.sendWebsocket((websocketInbound, websocketOutbound) -> {
			ReactorNettyWebSocketSession session = new ReactorNettyWebSocketSession(
					websocketInbound,
					websocketOutbound,
					new HandshakeInfo(
							handshakeInfo.getUri(),
							handshakeInfo.getHeaders(),
							handshakeInfo.getCookies(),
							Mono.just(auth),
							handshakeInfo.getSubProtocol(),
							handshakeInfo.getRemoteAddress(),
							handshakeInfo.getAttributes(),
							handshakeInfo.getLogPrefix()
					),
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

	private Authentication getAuth(HandshakeInfo info) {
		var uri = info.getUri().toString();
		int idx = uri.indexOf("?token=");
		if (idx == -1) return null;
		return this.tokenService.getAuthentication(uri.substring(idx + 7));
	}
}
