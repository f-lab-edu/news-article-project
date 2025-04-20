package com.example.config;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;

public class RedisTestContainerConfig implements BeforeAllCallback, ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Container
    private static final GenericContainer<?> redisContainer = new GenericContainer<>("redis:7.2.7")
            .withExposedPorts(6379)
            .waitingFor(Wait.forListeningPort());

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        redisContainer.start();
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        if (!redisContainer.isRunning()) {
            redisContainer.start();
        }

        TestPropertyValues.of(
                "spring.data.redis.host=" + redisContainer.getHost(),
                "spring.data.redis.port=" + redisContainer.getMappedPort(6379)
        ).applyTo(applicationContext.getEnvironment());
    }
}
