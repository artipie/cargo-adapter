/*
 * The MIT License (MIT) Copyright (c) 2020-2022 artipie.com
 * https://github.com/artipie/cargo-adapter/LICENSE
 */
package com.artipie.cargo.http.auth;

import com.artipie.http.auth.AuthScheme;
import com.artipie.http.auth.Authentication;
import com.artipie.http.auth.TokenAuthentication;
import com.artipie.http.headers.Authorization;
import com.artipie.http.rq.RqHeaders;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Conda token auth scheme.
 * @since 0.1
 */
public final class TokenAuthScheme implements AuthScheme {

    /**
     * Token authentication prefix.
     */
    public static final String NAME = "token";

    /**
     * Token authentication.
     */
    private final TokenAuthentication auth;

    /**
     * Ctor.
     * @param auth Token authentication
     */
    public TokenAuthScheme(final TokenAuthentication auth) {
        this.auth = auth;
    }

    @Override
    public CompletionStage<Result> authenticate(final Iterable<Map.Entry<String, String>> headers,
        final String line) {
        return this.user(headers).thenApply(
            user -> user.<Result>map(Success::new).orElse(new Failure())
        );
    }

    /**
     * Obtains user from authentication header or from request line.
     *
     * @param headers Headers
     * @return User, empty if not authenticated
     */
    private CompletionStage<Optional<Authentication.User>> user(
        final Iterable<Map.Entry<String, String>> headers
    ) {
        return new RqHeaders(headers, Authorization.NAME).stream()
            .findFirst()
            .map(Authorization::new)
            .filter(hdr -> hdr.scheme().equals(TokenAuthScheme.NAME))
            .map(hdr -> new Authorization.Token(hdr.credentials()).token())
            .map(this.auth::user)
            .orElse(CompletableFuture.completedFuture(Optional.empty()));
    }

    /**
     * Successful result with authenticated user.
     *
     * @since 0.5
     */
    private static class Success implements Result {

        /**
         * Authenticated user.
         */
        private final Authentication.User usr;

        /**
         * Ctor.
         *
         * @param user Authenticated user.
         */
        Success(final Authentication.User user) {
            this.usr = user;
        }

        @Override
        public Optional<Authentication.User> user() {
            return Optional.of(this.usr);
        }

        @Override
        public String challenge() {
            return TokenAuthScheme.NAME;
        }
    }

    /**
     * Failed result without authenticated user.
     *
     * @since 0.5
     */
    private static class Failure implements Result {

        @Override
        public Optional<Authentication.User> user() {
            return Optional.empty();
        }

        @Override
        public String challenge() {
            return TokenAuthScheme.NAME;
        }
    }
}
