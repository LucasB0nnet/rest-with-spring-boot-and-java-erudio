package br.com.erudio.integrationtests.testcontainers;

import java.util.Map;
import java.util.stream.Stream;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.lifecycle.Startables;

@ContextConfiguration(initializers = AbstractIntegrationTest.Initializer.class)
public class AbstractIntegrationTest {

	static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

		static MySQLContainer<?> container = new MySQLContainer<>("mysql:9.1.0");

		private static void startContainers() {
			Startables.deepStart(Stream.of(container)).join();
		}

		private static Map<String, String> createConnectionConfiguration() {
			return Map.of("spring.datasource.url", container.getJdbcUrl(), "spring.datasource.username",
					container.getUsername(), "spring.datasource.password", container.getPassword(),
					"server.port", "8889");
		}

		@Override
		public void initialize(ConfigurableApplicationContext applicationContext) {
			startContainers();
			ConfigurableEnvironment environment = applicationContext.getEnvironment();
			MapPropertySource testcontainers = new MapPropertySource("testcontainers",
					(Map) createConnectionConfiguration());
			environment.getPropertySources().addFirst(testcontainers);
		}

	}

}
