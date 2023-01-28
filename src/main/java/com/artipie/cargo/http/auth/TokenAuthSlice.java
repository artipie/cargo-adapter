/*
 * The MIT License (MIT) Copyright (c) 2020-2023 artipie.com
 * https://github.com/artipie/cargo-adapter/LICENSE
 */
package com.artipie.cargo.http.auth;

import com.artipie.cargo.AuthTokens;
import com.artipie.http.Slice;
import com.artipie.http.auth.AuthSlice;
import com.artipie.http.auth.Permission;

/**
 * Token authentication slice.
 * @since 0.1
 */
public final class TokenAuthSlice extends Slice.Wrap {

    /**
     * Ctor.
     * @param origin Origin slice
     * @param perm Permissions
     * @param tokens Token authentication
     */
    public TokenAuthSlice(
        final Slice origin, final Permission perm, final AuthTokens tokens
    ) {
        super(new AuthSlice(origin, new TokenAuthScheme(new TokenAuth(tokens)), perm));
    }
}
