/*
 * The MIT License (MIT) Copyright (c) 2020-2022 artipie.com
 * https://github.com/artipie/cargo-adapter/LICENSE
 */
package com.artipie.cargo.http;

import com.artipie.http.Response;
import com.artipie.http.rs.common.RsJson;
import com.google.common.collect.Lists;
import java.util.Collection;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonStructure;

/**
 * Cargo error response.
 * <a href="https://doc.rust-lang.org/cargo/reference/registries.html#web-api">Documentation</a>.
 * @since 0.1
 */
public final class RsError extends Response.Wrap {

    /**
     * Ctor.
     * @param messages Error messages
     */
    public RsError(final Collection<String> messages) {
        super(new RsJson(RsError.format(messages)));
    }

    /**
     * Ctor.
     * @param messages Error messages
     */
    public RsError(final String... messages) {
        this(Lists.newArrayList(messages));
    }

    /**
     * Format proper json with provided error messages.
     * @param messages Error messages
     * @return Json structure
     */
    private static JsonStructure format(final Collection<String> messages) {
        JsonArrayBuilder arr = Json.createArrayBuilder();
        for (final String msg : messages) {
            arr = arr.add(Json.createObjectBuilder().add("detail", msg).build());
        }
        return Json.createObjectBuilder().add("errors", arr).build();
    }

}
