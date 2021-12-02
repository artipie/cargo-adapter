/*
 * The MIT License (MIT) Copyright (c) 2020-2021 artipie.com
 * https://github.com/artipie/cargo-adapter/LICENSE
 */
package com.artipie.cargo;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link AuthTokens.TokenItem}.
 * @since 0.1
 */
class AuthTokensTest {

    @Test
    void returnsUserName() {
        final String name = "Alex";
        MatcherAssert.assertThat(
            new AuthTokens.TokenItem("abc123", name, Instant.MAX).userName(),
            new IsEqual<>(name)
        );
    }

    @Test
    void returnsToken() {
        final String token = "xyz098";
        MatcherAssert.assertThat(
            new AuthTokens.TokenItem(token, "Jane", Instant.MAX).token(),
            new IsEqual<>(token)
        );
    }

    @Test
    void returnsTrueIfExpired() {
        MatcherAssert.assertThat(
            new AuthTokens.TokenItem("000", "Alice", Instant.now().minus(1, ChronoUnit.DAYS))
                .expired(),
            new IsEqual<>(true)
        );
    }

    @Test
    void returnsFalseIfNotExpired() {
        MatcherAssert.assertThat(
            new AuthTokens.TokenItem("999", "John", Instant.now().plus(1, ChronoUnit.DAYS))
                .expired(),
            new IsEqual<>(false)
        );
    }

}
