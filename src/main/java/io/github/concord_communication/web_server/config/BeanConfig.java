package io.github.concord_communication.web_server.config;

import de.mkammerer.snowflakeid.SnowflakeIdGenerator;
import de.mkammerer.snowflakeid.options.Options;
import de.mkammerer.snowflakeid.structure.Structure;
import de.mkammerer.snowflakeid.time.MonotonicTimeSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Configuration
public class BeanConfig {
	@Bean
	public SnowflakeIdGenerator snowflakeIdGenerator() {
		return SnowflakeIdGenerator.createCustom(
				1,
				new MonotonicTimeSource(ZonedDateTime.of(2021, 10, 10, 12, 0, 0, 0, ZoneOffset.UTC).toInstant()),
				Structure.createDefault(),
				Options.createDefault()
		);
	}
}
