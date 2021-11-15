package io.github.concord_communication.web_server.service.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.concord_communication.web_server.model.user.User;
import io.github.concord_communication.web_server.model.websocket.ChatMessages;
import io.github.concord_communication.web_server.model.websocket.ClientMessageEvent;
import io.github.concord_communication.web_server.model.websocket.MessageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static io.github.concord_communication.web_server.util.JsonUtils.JSON;

/**
 * Component for serializing and deserializing messages from websocket
 * connections.
 */
@Component
@Slf4j
public class MessageSerializer {
	private static final ObjectWriter writer = JSON.writer();
	private static final ObjectReader reader = JSON.reader();

	private final Map<String, Class<?>> messageTypes = new HashMap<>();

	public MessageSerializer() {
		registerTypes(ChatMessages.Written.class, ChatMessages.Typing.class, ChatMessages.Reaction.class);
	}

	public WebSocketMessage serialize(Object obj, WebSocketSession session) {
		var messageType = obj.getClass().getAnnotation(MessageType.class);
		try {
			if (messageType == null) {
				return session.textMessage(writer.writeValueAsString(obj));
			} else {
				ObjectNode node = JSON.valueToTree(obj);
				node.put("type", messageType.value());
				return session.textMessage(node.toString());
			}
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public Mono<ClientMessageEvent> deserialize(WebSocketMessage msg, User user) {
		return Mono.fromCallable(() -> {
			JsonNode node = reader.readTree(msg.getPayloadAsText());
			var typeNode = node.get("type");
			if (typeNode == null || typeNode.isMissingNode() || !typeNode.isTextual()) {
				log.warn("Missing or non-textual message type received.");
				return null;
			}
			String type = typeNode.asText();
			var messageType = messageTypes.get(type);
			if (messageType == null) return null;
			try {
				return new ClientMessageEvent(user, type, reader.treeToValue(node, messageType));
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		});
	}

	/**
	 * Registers a set of types such that this serializer will be able to handle
	 * reading and writing them.
	 * @param types The types to register.
	 * @throws IllegalArgumentException if a type is not annotated with {@link MessageType},
	 * since only classes annotated with that are allowed.
	 */
	private void registerTypes(Class<?>... types) {
		for (var type : types) {
			var annotation = type.getAnnotation(MessageType.class);
			if (annotation == null) throw new IllegalArgumentException("Invalid message type: " + type.getSimpleName());
			messageTypes.put(annotation.value(), type);
		}
	}
}
