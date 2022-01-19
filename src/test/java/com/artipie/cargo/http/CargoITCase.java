/*
 * The MIT License (MIT) Copyright (c) 2020-2021 artipie.com
 * https://github.com/artipie/cargo-adapter/LICENSE
 */
package com.artipie.cargo.http;

import com.artipie.asto.memory.InMemoryStorage;
import com.artipie.http.misc.RandomFreePort;
import com.artipie.http.slice.LoggingSlice;
import com.artipie.vertx.VertxSliceServer;
import com.jcabi.log.Logger;
import io.vertx.reactivex.core.Vertx;
import java.nio.file.Files;
import java.nio.file.Path;
import org.cactoos.list.ListOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.text.StringContainsInOrder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.testcontainers.Testcontainers;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;

/**
 * Integration test for cargo.
 * @since 0.1
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class CargoITCase {

    /**
     * Vertx instance.
     */
    private static final Vertx VERTX = Vertx.vertx();

    /**
     * Temporary directory for all tests.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @TempDir
    Path tmp;

    /**
     * Vertx slice server instance.
     */
    private VertxSliceServer server;

    /**
     * Container.
     */
    private GenericContainer<?> cntn;

    @BeforeEach
    void init() throws Exception {
        final int port = new RandomFreePort().get();
        final String url = String.format("http://host.testcontainers.internal:%d", port);
        this.server = new VertxSliceServer(
            CargoITCase.VERTX,
            new LoggingSlice(new CargoSlice(new InMemoryStorage())),
            port
        );
        this.server.start();
        Testcontainers.exposeHostPorts(port);
        this.tmp.resolve(".cargo").toFile().mkdir();
        Files.write(
            this.tmp.resolve(".cargo/config.toml"),
            String.join(
                "\n",
                "[registries.artipie]",
                String.format("index = \"%s\"", url),
                "token = \"abc123\"",
                "[registry]",
                "default = \"artipie\""
            ).getBytes()
        );
        this.cntn = new GenericContainer<>("rust:slim")
            .withCommand("tail", "-f", "/dev/null")
            .withEnv("CARGO_NET_GIT_FETCH_WITH_CLI", "true")
            .withEnv("GIT_TRACE", "true")
            .withWorkingDirectory("/home/")
            .withFileSystemBind(this.tmp.toString(), "/home");
        this.cntn.start();
        this.exec("apt", "update");
        this.exec("apt", "install", "-y", "git");
        this.exec("git", "--version");
    }

    @AfterEach
    void stop() {
        this.server.stop();
        this.cntn.stop();
    }

    @Test
    void canPush() throws Exception {
        this.exec("cargo", "version");
        this.exec("cargo", "new", "hello_world");
        MatcherAssert.assertThat(
            this.exec(
                "cargo", "publish", "--verbose", "--manifest-path", "./hello_world/Cargo.toml"
            ),
            new StringContainsInOrder(
                new ListOf<String>(
                    "Upload successful", "http://host.testcontainers.internal"
                )
            )
        );
    }

    private String exec(final String... command) throws Exception {
        final Container.ExecResult res = this.cntn.execInContainer(command);
        Logger.debug(this, "Command:\n%s\nResult:\n%s", String.join(" ", command), res.toString());
        return res.toString();
    }
}
