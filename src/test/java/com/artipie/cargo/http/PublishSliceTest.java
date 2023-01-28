/*
 * The MIT License (MIT) Copyright (c) 2020-2023 artipie.com
 * https://github.com/artipie/cargo-adapter/LICENSE
 */
package com.artipie.cargo.http;

import com.artipie.asto.Content;
import com.artipie.asto.Key;
import com.artipie.asto.Storage;
import com.artipie.asto.memory.InMemoryStorage;
import com.artipie.cargo.meta.MetadataKey;
import com.artipie.http.Headers;
import com.artipie.http.hm.RsHasBody;
import com.artipie.http.hm.RsHasStatus;
import com.artipie.http.hm.SliceHasResponse;
import com.artipie.http.rq.RequestLine;
import com.artipie.http.rq.RqMethod;
import com.artipie.http.rs.RsStatus;
import java.nio.charset.StandardCharsets;
import javax.json.Json;
import javax.json.JsonValue;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link PublishSlice}.
 * @since 0.1
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 * @checkstyle MagicNumberCheck (500 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class PublishSliceTest {

    /**
     * Test storage.
     */
    private Storage asto;

    @BeforeEach
    void init() {
        this.asto = new InMemoryStorage();
    }

    @Test
    void storesData() {
        final String name = "foo";
        final String vers = "0.1.2";
        final String json = Json.createObjectBuilder().add("name", name).add("vers", vers)
            .build().toString();
        final String data = "cargo package";
        MatcherAssert.assertThat(
            "Failed to response with 200 OK and json body",
            new PublishSlice(this.asto),
            new SliceHasResponse(
                Matchers.allOf(
                    new RsHasStatus(RsStatus.OK),
                    new RsHasBody(
                        Json.createObjectBuilder()
                            .add(
                                "warnings",
                                Json.createObjectBuilder()
                                    .add("invalid_categories", JsonValue.EMPTY_JSON_ARRAY)
                                    .add("invalid_badges", JsonValue.EMPTY_JSON_ARRAY)
                                    .add("other", JsonValue.EMPTY_JSON_ARRAY)
                            ).build().toString().getBytes(StandardCharsets.UTF_8)
                    )
                ),
                new RequestLine(RqMethod.PUT, "/api/v1/crates/new"),
                Headers.EMPTY,
                new Content.From(
                    String.format(
                        "%s%s%s%s",
                        String.format("%04x", json.length() + 4), json,
                        String.format("%04x", data.length() + 4), data
                    ).getBytes(StandardCharsets.UTF_8)
                )
            )
        );
        MatcherAssert.assertThat(
            "Failed to store package metadata",
            this.asto.exists(new MetadataKey(name).get()).join(),
            new IsEqual<>(true)
        );
        MatcherAssert.assertThat(
            "Failed to store package",
            this.asto.exists(new Key.From(name, vers, "crate")).join(),
            new IsEqual<>(true)
        );
    }

}
