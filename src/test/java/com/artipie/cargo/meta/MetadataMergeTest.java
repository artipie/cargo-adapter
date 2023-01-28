/*
 * The MIT License (MIT) Copyright (c) 2020-2023 artipie.com
 * https://github.com/artipie/cargo-adapter/LICENSE
 */
package com.artipie.cargo.meta;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link MetadataMerge}.
 * @since 0.1
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class MetadataMergeTest {

    @Test
    void addsWhenInputIsAbsent() {
        final ByteArrayOutputStream res = new ByteArrayOutputStream();
        final String line = "new package metadata line";
        new MetadataMerge(line).accept(Optional.empty(), res);
        MatcherAssert.assertThat(
            res.toString(),
            new IsEqual<>(line)
        );
    }

    @Test
    void appendsLine() {
        final ByteArrayOutputStream res = new ByteArrayOutputStream();
        final ByteArrayInputStream input = new ByteArrayInputStream(
            String.join(
                "\n",
                "package version 0.1 metadata",
                "package version 0.1.3 metadata",
                "package version 0.2 metadata"
            ).getBytes(StandardCharsets.UTF_8)
        );
        final String line = "new package version metadata line";
        new MetadataMerge(line).accept(Optional.of(input), res);
        MatcherAssert.assertThat(
            res.toString(),
            new IsEqual<>(
                String.join(
                    "\n",
                    "package version 0.1 metadata",
                    "package version 0.1.3 metadata",
                    "package version 0.2 metadata",
                    "new package version metadata line"
                )
            )
        );
    }

}
