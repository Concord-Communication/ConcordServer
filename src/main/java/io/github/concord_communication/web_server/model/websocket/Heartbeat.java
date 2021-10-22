package io.github.concord_communication.web_server.model.websocket;

/**
 * The heartbeat message is a simple ping that's sent periodically to ensure
 * the client is still connected and responsive.
 */
@MessageType("heartbeat")
@Deprecated
public record Heartbeat(long timestamp) {}
