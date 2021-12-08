/*
 * The MIT License (MIT) Copyright (c) 2020-2021 artipie.com
 * https://github.com/artipie/cargo-adapter/LICENSE
 */
package com.artipie.cargo.http;

import com.artipie.http.Response;
import com.artipie.http.Slice;
import java.nio.ByteBuffer;
import java.util.Map;
import org.apache.commons.lang3.NotImplementedException;
import org.reactivestreams.Publisher;

/**
 * Cargo publish slice.
 * <a href="https://doc.rust-lang.org/cargo/reference/registries.html#publish">Publish endpoint</a>.
 * @since 0.1
 */
public final class PublishSlice implements Slice {

    @Override
    public Response response(final String line, final Iterable<Map.Entry<String, String>> headers,
        final Publisher<ByteBuffer> body) {
        throw new NotImplementedException("Not implemented yet");
    }
}
