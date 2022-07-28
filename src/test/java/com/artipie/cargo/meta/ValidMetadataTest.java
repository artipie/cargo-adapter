/*
 * The MIT License (MIT) Copyright (c) 2020-2022 artipie.com
 * https://github.com/artipie/cargo-adapter/LICENSE
 */
package com.artipie.cargo.meta;

import com.artipie.ArtipieException;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test for {@link ValidMetadata}.
 * @since 0.1
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class ValidMetadataTest {

    @ParameterizedTest
    @ValueSource(strings = {"a", "a1", "a-b_c", "z_2-uy"})
    void returnsName(final String name) {
        MatcherAssert.assertThat(
            new ValidMetadata(Json.createObjectBuilder().add("name", name).build()).validName(),
            new IsEqual<>(name)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"a/", "a(sdsd)="})
    void validatesName(final String name) {
        Assertions.assertThrows(
            ArtipieException.class,
            () -> new ValidMetadata(Json.createObjectBuilder().add("name", name).build())
                .validName()
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"1.0.0", "0.1.0", "2.0.0-alpha.1+001", "1.0.0-alpha.1"})
    void returnsVersion(final String ver) {
        MatcherAssert.assertThat(
            new ValidMetadata(Json.createObjectBuilder().add("vers", ver).build()).validVersion(),
            new IsEqual<>(ver)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.0", "xyz"})
    void validatesVersion(final String ver) {
        Assertions.assertThrows(
            ArtipieException.class,
            () -> new ValidMetadata(Json.createObjectBuilder().add("vers", ver).build())
                .validVersion()
        );
    }

}
