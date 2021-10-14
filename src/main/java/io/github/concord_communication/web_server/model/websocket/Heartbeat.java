package io.github.concord_communication.web_server.model.websocket;

@MessageType("heartbeat")
public record Heartbeat(long timestamp) {}
