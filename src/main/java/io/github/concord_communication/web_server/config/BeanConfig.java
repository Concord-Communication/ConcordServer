package io.github.concord_communication.web_server.config;

import de.mkammerer.snowflakeid.SnowflakeIdGenerator;
import de.mkammerer.snowflakeid.options.Options;
import de.mkammerer.snowflakeid.structure.Structure;
import de.mkammerer.snowflakeid.time.MonotonicTimeSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * Configuration that is used for defining some extra beans that other config
 * classes may depend on.
 */
@Configuration
public class BeanConfig {
	/**
	 * Defines an id generator bean which can be used to generate monotonic,
	 * unique 64-bit identifiers for entities.
	 * @return The snowflake id generator.
	 */
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
