/*
 * The MIT License (MIT) Copyright (c) 2020-2023 artipie.com
 * https://github.com/artipie/cargo-adapter/LICENSE
 */
package com.artipie.cargo.http.auth;

import com.artipie.cargo.AuthTokens;
import com.artipie.http.auth.Authentication;
import com.artipie.http.auth.TokenAuthentication;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

/**
 * Simple in memory implementation of {@link TokenAuthentication}.
 * @since 0.1
 */
public final class TokenAuth implements TokenAuthentication {

    /**
     * Tokens.
     */
    private final AuthTokens tokens;

    /**
     * Ctor.
     * @param tokens Tokens and users
     */
    public TokenAuth(final AuthTokens tokens) {
        this.tokens = tokens;
    }

    @Override
    public CompletionStage<Optional<Authentication.User>> user(final String token) {
        return this.tokens.get(token)
            .thenApply(tkn -> tkn.map(item -> new Authentication.User(item.userName())));
    }
}
