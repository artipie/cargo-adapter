/*
 * The MIT License (MIT) Copyright (c) 2020-2023 artipie.com
 * https://github.com/artipie/cargo-adapter/LICENSE
 */
package com.artipie.cargo.meta;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link MetadataYank}.
 * @since 0.1
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class MetadataYankTest {

    @Test
    void updatesYank() {
        final ByteArrayOutputStream res = new ByteArrayOutputStream();
        final ByteArrayInputStream input = new ByteArrayInputStream(
            String.join(
                System.lineSeparator(),
                // @checkstyle LineLengthCheck (2 lines)
                Json.createObjectBuilder().add("name", "test").add("vers", "0.1.2").add("yank", true).build().toString(),
                Json.createObjectBuilder().add("name", "test").add("vers", "0.2.4").build().toString()
            ).getBytes(StandardCharsets.UTF_8)
        );
        MatcherAssert.assertThat(
            "Returns true",
            new MetadataYank("0.1.2", false).apply(Optional.of(input), res),
            new IsEqual<>(true)
        );
        MatcherAssert.assertThat(
            "Yank is not added",
            res.toByteArray(),
            new IsEqual<>(
                String.join(
                    System.lineSeparator(),
                    // @checkstyle LineLengthCheck (2 lines)
                    Json.createObjectBuilder().add("name", "test").add("vers", "0.1.2").add("yank", false).build().toString(),
                    Json.createObjectBuilder().add("name", "test").add("vers", "0.2.4").build().toString()
                ).getBytes(StandardCharsets.UTF_8)
            )
        );
    }

    @Test
    void addsYank() {
        final ByteArrayOutputStream res = new ByteArrayOutputStream();
        final ByteArrayInputStream input = new ByteArrayInputStream(
            String.join(
                System.lineSeparator(),
                // @checkstyle LineLengthCheck (3 lines)
                Json.createObjectBuilder().add("name", "foo").add("vers", "0.0.1").build().toString(),
                Json.createObjectBuilder().add("name", "foo").add("vers", "0.0.3").build().toString(),
                Json.createObjectBuilder().add("name", "foo").add("vers", "0.1.1").build().toString()
            ).getBytes(StandardCharsets.UTF_8)
        );
        MatcherAssert.assertThat(
            "Returns true",
            new MetadataYank("0.0.3", true).apply(Optional.of(input), res),
            new IsEqual<>(true)
        );
        MatcherAssert.assertThat(
            "Yank is not added",
            res.toByteArray(),
            new IsEqual<>(
                String.join(
                    System.lineSeparator(),
                    // @checkstyle LineLengthCheck (3 lines)
                    Json.createObjectBuilder().add("name", "foo").add("vers", "0.0.1").build().toString(),
                    Json.createObjectBuilder().add("name", "foo").add("vers", "0.0.3").add("yank", true).build().toString(),
                    Json.createObjectBuilder().add("name", "foo").add("vers", "0.1.1").build().toString()
                ).getBytes(StandardCharsets.UTF_8)
            )
        );
    }

    @Test
    void doesNothingIfVersionNotFound() {
        final ByteArrayOutputStream res = new ByteArrayOutputStream();
        final byte[] data = String.join(System.lineSeparator(), "a", "b", "123")
            .getBytes(StandardCharsets.UTF_8);
        final ByteArrayInputStream input = new ByteArrayInputStream(data);
        MatcherAssert.assertThat(
            "Returns false",
            new MetadataYank("0.1", true).apply(Optional.of(input), res),
            new IsEqual<>(false)
        );
        MatcherAssert.assertThat(
            "Data are not changed",
            res.toByteArray(),
            new IsEqual<>(data)
        );
    }

}
