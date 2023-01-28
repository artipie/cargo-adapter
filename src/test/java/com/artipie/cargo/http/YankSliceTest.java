/*
 * The MIT License (MIT) Copyright (c) 2020-2023 artipie.com
 * https://github.com/artipie/cargo-adapter/LICENSE
 */
package com.artipie.cargo.http;

import com.artipie.asto.Content;
import com.artipie.asto.Storage;
import com.artipie.asto.blocking.BlockingStorage;
import com.artipie.asto.memory.InMemoryStorage;
import com.artipie.cargo.meta.MetadataKey;
import com.artipie.http.hm.RsHasBody;
import com.artipie.http.hm.RsHasStatus;
import com.artipie.http.hm.SliceHasResponse;
import com.artipie.http.rq.RequestLine;
import com.artipie.http.rq.RqMethod;
import com.artipie.http.rs.RsStatus;
import java.nio.charset.StandardCharsets;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Test for {@link YankSlice}.
 * @since 0.1
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class YankSliceTest {

    /**
     * Test storage.
     */
    private Storage asto;

    @BeforeEach
    void init() {
        this.asto = new InMemoryStorage();
    }

    @ParameterizedTest
    @CsvSource({
        "yank,true,DELETE",
        "unyank,false,PUT"
    })
    void yankesMetadataAndReturnsOk(final String cmd, final boolean res, final RqMethod method) {
        final String name = "my-project";
        this.asto.save(
            new MetadataKey(name).get(),
            new Content.From(
                Json.createObjectBuilder().add("name", name).add("vers", "0.1.2").build()
                    .toString().getBytes(StandardCharsets.UTF_8)
            )
        ).join();
        MatcherAssert.assertThat(
            "Failed to response with OK status and json body",
            new YankSlice(this.asto),
            new SliceHasResponse(
                Matchers.allOf(
                    new RsHasStatus(RsStatus.OK),
                    new RsHasBody("{\"ok\":true}".getBytes(StandardCharsets.UTF_8))
                ),
                new RequestLine(
                    method, String.format("/api/v1/crates/%s/0.1.2/%s", name, cmd)
                )
            )
        );
        MatcherAssert.assertThat(
            "Project metadata file was updated",
            new BlockingStorage(this.asto).value(new MetadataKey(name).get()),
            new IsEqual<>(
                Json.createObjectBuilder().add("name", name).add("vers", "0.1.2")
                    .add("yank", res).build().toString().getBytes(StandardCharsets.UTF_8)
            )
        );
    }

    @Test
    void returnsErrorResponseWhenRequestIsInvalid() {
        MatcherAssert.assertThat(
            new YankSlice(this.asto),
            new SliceHasResponse(
                Matchers.allOf(
                    new RsHasStatus(RsStatus.OK),
                    new RsHasBody(
                        // @checkstyle LineLengthCheck (2 lines)
                        "{\"errors\":[{\"detail\":\"Invalid request: this endpoint processes yank and unyank requests\"}]}".getBytes(StandardCharsets.UTF_8)
                    )
                ),
                new RequestLine(RqMethod.DELETE, "/any")
            )
        );
    }

    @Test
    void returnsErrorResponseWhenMetadataNotFound() {
        MatcherAssert.assertThat(
            new YankSlice(this.asto),
            new SliceHasResponse(
                Matchers.allOf(
                    new RsHasStatus(RsStatus.OK),
                    new RsHasBody(
                        // @checkstyle LineLengthCheck (2 lines)
                        "{\"errors\":[{\"detail\":\"com.artipie.ArtipieException: Package metadata does not exist!\"}]}".getBytes(StandardCharsets.UTF_8)
                    )
                ),
                new RequestLine(RqMethod.DELETE, "/api/v1/crates/foo/0.1.0/yank")
            )
        );
    }

}
