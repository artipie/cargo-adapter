/*
 * The MIT License (MIT) Copyright (c) 2020-2021 artipie.com
 * https://github.com/artipie/cargo-adapter/LICENSE
 */
package com.artipie.cargo.http;

import com.artipie.asto.Storage;
import com.artipie.asto.streams.StorageValuePipeline;
import com.artipie.cargo.meta.MetadataKey;
import com.artipie.cargo.meta.MetadataYank;
import com.artipie.http.Response;
import com.artipie.http.Slice;
import com.artipie.http.async.AsyncResponse;
import com.artipie.http.rq.RequestLineFrom;
import com.artipie.http.rs.common.RsJson;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.json.Json;
import org.reactivestreams.Publisher;

/**
 * Slice to process `yank` and `unyank` cargo requests.<br/>
 * See<br/>
 * https://doc.rust-lang.org/cargo/reference/registries.html#yank<br/>
 * https://doc.rust-lang.org/cargo/reference/registries.html#unyank
 * @since 0.1
 */
final class YankSlice implements Slice {

    /**
     * Pattern to YankSlice path.
     */
    static final Pattern YANK_PTRN =
        Pattern.compile("/api/v1/crates/(?<name>.+)/(?<version>.+)/(?<cmd>yank|unyank)");

    /**
     * Abstract storage.
     */
    private final Storage asto;

    /**
     * Ctor.
     * @param asto Abstract storage
     */
    YankSlice(final Storage asto) {
        this.asto = asto;
    }

    @Override
    @SuppressWarnings("PMD.ConfusingTernary")
    public Response response(final String line, final Iterable<Map.Entry<String, String>> headers,
        final Publisher<ByteBuffer> body) {
        final Response res;
        final Matcher matcher = YankSlice.YANK_PTRN
            .matcher(new RequestLineFrom(line).uri().toString());
        if (matcher.matches()) {
            final String version = matcher.group("version");
            res = new AsyncResponse(
                new StorageValuePipeline<Boolean>(
                    this.asto, new MetadataKey(matcher.group("name")).get()
                ).processWithResult(
                    new MetadataYank(version, matcher.group("cmd").equals("yank"))
                ).handle(
                    (yank, throwable) -> {
                        final Response resp;
                        if (throwable != null) {
                            resp = new RsError(throwable.getMessage());
                        } else if (yank) {
                            resp = new RsJson(Json.createObjectBuilder().add("ok", yank));
                        } else {
                            resp = new RsError(
                                String.format(
                                    "Failed to yank/unyank package, package version %s not found",
                                    version
                                )
                            );
                        }
                        return resp;
                    }
                )
            );
        } else {
            res = new RsError("Invalid request: this endpoint processes yank and unyank requests");
        }
        return res;
    }
}
