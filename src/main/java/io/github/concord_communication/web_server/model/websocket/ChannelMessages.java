package io.github.concord_communication.web_server.model.websocket;


import io.github.concord_communication.web_server.api.dto.ChannelResponse;
import io.github.concord_communication.web_server.model.Channel;

public final class ChannelMessages {
	// Hide constructor.
	private ChannelMessages() {}

	@MessageType("channel_created")
	public record Created(ChannelResponse channel) {
		public Created(Channel channel) {
			this(new ChannelResponse(channel));
		}
	}

	@MessageType("channel_deleted")
	public record Deleted(long channelId) {}

	@MessageType("channels_updated")
	public record Updated(ChannelResponse channel) {}
}
