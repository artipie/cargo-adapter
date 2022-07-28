/*
 * The MIT License (MIT) Copyright (c) 2020-2022 artipie.com
 * https://github.com/artipie/cargo-adapter/LICENSE
 */
package com.artipie.cargo.http;

import com.artipie.asto.Content;
import com.artipie.asto.Key;
import com.artipie.asto.Storage;
import com.artipie.asto.ext.PublisherAs;
import com.artipie.asto.streams.StorageValuePipeline;
import com.artipie.cargo.meta.MetadataKey;
import com.artipie.cargo.meta.MetadataMerge;
import com.artipie.cargo.meta.ValidMetadata;
import com.artipie.git.sdk.GitRequest;
import com.artipie.http.Response;
import com.artipie.http.Slice;
import com.artipie.http.async.AsyncResponse;
import com.artipie.http.rs.common.RsJson;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonValue;
import org.reactivestreams.Publisher;

/**
 * Cargo publish slice.
 * <a href="https://doc.rust-lang.org/cargo/reference/registries.html#publish">Publish endpoint</a>.
 * @since 0.1
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class PublishSlice implements Slice {

    /**
     * Abstract storage.
     */
    private final Storage asto;

    /**
     * Ctor.
     * @param asto Abstract storage
     */
    public PublishSlice(final Storage asto) {
        this.asto = asto;
    }

    @Override
    public Response response(final String line, final Iterable<Map.Entry<String, String>> headers,
        final Publisher<ByteBuffer> body) {
        return new AsyncResponse(
            new PublisherAs(body).asciiString().thenApply(data -> GitRequest.parse(data).parts())
                .thenCompose(
                    list -> {
                        final ValidMetadata meta = new ValidMetadata(list.get(0));
                        return new StorageValuePipeline<Void>(
                            this.asto, new MetadataKey(meta.validName()).get()
                        ).process(new MetadataMerge(list.get(0))).thenCompose(
                            nothing -> this.asto.save(
                                new Key.From(meta.validName(), meta.validVersion(), "crate"),
                                new Content.From(list.get(1).getBytes(StandardCharsets.UTF_8))
                            )
                        );
                    }
                ).handle(
                    (ignored, throwable) -> {
                        final Response response;
                        if (throwable == null) {
                            response = new RsJson(
                                Json.createObjectBuilder()
                                    .add(
                                        "warnings",
                                        Json.createObjectBuilder()
                                            .add("invalid_categories", JsonValue.EMPTY_JSON_ARRAY)
                                            .add("invalid_badges", JsonValue.EMPTY_JSON_ARRAY)
                                            .add("other", JsonValue.EMPTY_JSON_ARRAY)
                                    )
                            );
                        } else {
                            response = new RsError(throwable.getMessage());
                        }
                        return response;
                    }
                )
        );
    }
}
