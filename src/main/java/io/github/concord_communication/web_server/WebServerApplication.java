package io.github.concord_communication.web_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main entrypoint for the Concord Server application.
 */
@SpringBootApplication
public class WebServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(WebServerApplication.class, args);
	}
}
