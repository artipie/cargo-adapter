/*
 * The MIT License (MIT) Copyright (c) 2020-2021 artipie.com
 * https://github.com/artipie/cargo-adapter/LICENSE
 */
package com.artipie.cargo.http.auth;

import com.artipie.http.Headers;
import com.artipie.http.auth.Authentication;
import com.artipie.http.auth.TokenAuthentication;
import com.artipie.http.headers.Authorization;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link TokenAuthScheme}.
 * @since 0.1
 */
class TokenAuthSchemeTest {

    /**
     * Test token.
     */
    private static final String TKN = "abc123";

    @Test
    void canAuthorizeByHeader() {
        MatcherAssert.assertThat(
            new TokenAuthScheme(new TestTokenAuth()).authenticate(
                new Headers.From(new Authorization.Token(TokenAuthSchemeTest.TKN)),
                "GET /not/used HTTP/1.1"
            ).toCompletableFuture().join().user().isPresent(),
            new IsEqual<>(true)
        );
    }

    @Test
    void doesNotAuthorizeIfTokenIsNotPresent() {
        MatcherAssert.assertThat(
            new TokenAuthScheme(new TestTokenAuth()).authenticate(
                Headers.EMPTY,
                "GET /any HTTP/1.1"
            ).toCompletableFuture().join().user().isPresent(),
            new IsEqual<>(false)
        );
    }

    @Test
    void doesNotAuthorizeByWrongTokenInHeader() {
        MatcherAssert.assertThat(
            new TokenAuthScheme(new TestTokenAuth()).authenticate(
                new Headers.From(new Authorization.Token("098xyz")),
                "GET /ignored HTTP/1.1"
            ).toCompletableFuture().join().user().isPresent(),
            new IsEqual<>(false)
        );
    }

    /**
     * Test token auth.
     * @since 0.1
     */
    private static final class TestTokenAuth implements TokenAuthentication {

        @Override
        public CompletionStage<Optional<Authentication.User>> user(final String token) {
            Optional<Authentication.User> res = Optional.empty();
            if (token.equals(TokenAuthSchemeTest.TKN)) {
                res = Optional.of(new Authentication.User("Alice"));
            }
            return CompletableFuture.completedFuture(res);
        }
    }
}
