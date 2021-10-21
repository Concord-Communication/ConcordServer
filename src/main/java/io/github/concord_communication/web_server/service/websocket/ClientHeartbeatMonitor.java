package io.github.concord_communication.web_server.service.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.socket.CloseStatus;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ClientHeartbeatMonitor {
	private final Map<Long, Long> heartbeats = new ConcurrentHashMap<>();
	private final Map<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();
	private final long heartbeatTimeoutSeconds;
	private final ClientBroadcastManager broadcastManager;

	public ClientHeartbeatMonitor(long heartbeatTimeoutSeconds, ClientBroadcastManager broadcastManager) {
		this.heartbeatTimeoutSeconds = heartbeatTimeoutSeconds;
		this.broadcastManager = broadcastManager;
		var executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleAtFixedRate(
				() -> checkUserHeartbeatStatus(executor),
				60,
				60,
				TimeUnit.SECONDS
		);
	}

	public void beginTracking(long userId, WebSocketSession session) {
		this.sessions.put(userId, session);
		registerHeartbeat(userId); // Register an initial heartbeat when the user connects.
	}

	public void endTracking(long userId) {
		this.heartbeats.remove(userId);
		this.sessions.remove(userId);
	}

	public void registerHeartbeat(long userId) {
		this.heartbeats.put(userId, System.currentTimeMillis());
	}

	private void checkUserHeartbeatStatus(ExecutorService executor) {
		long now = System.currentTimeMillis();
		List<Long> inactiveUsers = heartbeats.entrySet().stream()
				.filter(e -> (now - e.getValue()) > (heartbeatTimeoutSeconds * 1000))
				.map(Map.Entry::getKey).toList();
		inactiveUsers.forEach(userId -> {
			log.info("Disconnecting user {} due to heartbeat unresponsiveness.", userId);
			broadcastManager.closeConnection(userId);
			var session = sessions.get(userId);
			if (session != null) {
				var mono = session.close(CloseStatus.PROTOCOL_ERROR);
				executor.submit(() -> {
					mono.block();
					log.info("Session has been closed.");
				});
			}
		});
	}
}
