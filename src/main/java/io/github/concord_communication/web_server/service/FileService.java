package io.github.concord_communication.web_server.service;

import de.mkammerer.snowflakeid.SnowflakeIdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsCriteria;
import org.springframework.data.mongodb.gridfs.ReactiveGridFsTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FileService {
	private final ReactiveGridFsTemplate gridFsTemplate;
	private final SnowflakeIdGenerator idGenerator;

	public Mono<Long> saveFile(ServerHttpRequest request) {
		String contentType = null;
		if (request.getHeaders().getContentType() != null) {
			contentType = request.getHeaders().getContentType().toString();
		}
		return saveFile(request.getBody(), contentType);
	}

	public Mono<Long> saveFile(Flux<DataBuffer> dataFlux, String contentType) {
		long id = idGenerator.next();
		return gridFsTemplate.store(dataFlux, String.valueOf(id), contentType).thenReturn(id);
	}

	public Mono<Void> removeFile(long id) {
		return gridFsTemplate.delete(Query.query(GridFsCriteria.whereFilename().is(String.valueOf(id))));
	}

	public Mono<Void> getImage(long imageId, Integer width, Integer height, ServerHttpRequest request, ServerHttpResponse response) {
		return gridFsTemplate.getResource(String.valueOf(imageId))
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
				.flatMap(resource -> {
					if (!resource.exists()) return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND));
					response.getHeaders().setContentType(MediaType.IMAGE_JPEG);
					response.setStatusCode(HttpStatus.OK);
					return response.writeWith(resource.getDownloadStream());
				});
	}
}
