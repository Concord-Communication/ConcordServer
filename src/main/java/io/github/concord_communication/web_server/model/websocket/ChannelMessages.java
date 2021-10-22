package io.github.concord_communication.web_server.model.websocket;


import io.github.concord_communication.web_server.api.dto.ChannelResponse;
import io.github.concord_communication.web_server.model.Channel;

/**
 * Container for messages that are sent regarding channel updates.
 */
public final class ChannelMessages {
	// Hide constructor.
	private ChannelMessages() {}

	/**
	 * Sent when a new channel is created.
	 */
	@MessageType("channel_created")
	public record Created(ChannelResponse channel) {
		public Created(Channel channel) {
			this(new ChannelResponse(channel));
		}
	}

	/**
	 * Sent when a channel is deleted.
	 */
	@MessageType("channel_deleted")
	public record Deleted(long channelId) {}

	/**
	 * Sent when a channel is updated.
	 */
	@MessageType("channels_updated")
	public record Updated(ChannelResponse channel) {}
}
