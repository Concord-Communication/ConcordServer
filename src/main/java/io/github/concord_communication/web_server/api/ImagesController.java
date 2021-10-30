package io.github.concord_communication.web_server.api;

import io.github.concord_communication.web_server.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/images")
@RequiredArgsConstructor
public class ImagesController {
	private final FileService fileService;

	@GetMapping(path = "/{imageId}")
	public Mono<Void> getImage(
			@PathVariable long imageId,
			@RequestParam(required = false, name = "w") Integer width,
			@RequestParam(required = false, name = "h") Integer height,
			ServerHttpRequest request,
			ServerHttpResponse response
	) {
		return fileService.getImage(imageId, width, height, request, response);
	}
}
