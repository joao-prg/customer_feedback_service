package com.joaogoncalves.feedback.testcontainers;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.MountableFile;

public class TestcontainersInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final GenericContainer<?> mongoDBContainer;

    static {
        mongoDBContainer = new GenericContainer<>("mongo:latest")
                .withExposedPorts(27017) // Expose MongoDB port
                .withCopyFileToContainer(
                        MountableFile.forClasspathResource("init-mongo.js"),
                        "/docker-entrypoint-initdb.d/init-mongo.js"
                )
                .waitingFor(Wait.forListeningPort())
                .withEnv("MONGO_INITDB_DATABASE", "feedback_db");

        mongoDBContainer.start();
    }

    @Override
    public void initialize(ConfigurableApplicationContext ctx) {
        final String mongoUri = String.format("mongodb://%s:%d/feedback_db",
                mongoDBContainer.getHost(),
                mongoDBContainer.getMappedPort(27017)
        );
        TestPropertyValues.of(
                "spring.data.mongodb.uri=" + mongoUri,
                "spring.data.mongodb.database=feedback_db"
        ).applyTo(ctx.getEnvironment());
    }
}
