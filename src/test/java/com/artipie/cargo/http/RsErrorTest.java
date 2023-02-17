/*
 * The MIT License (MIT) Copyright (c) 2020-2023 artipie.com
 * https://github.com/artipie/cargo-adapter/LICENSE
 */
package com.artipie.cargo.http;

import com.artipie.http.headers.ContentLength;
import com.artipie.http.headers.ContentType;
import com.artipie.http.hm.RsHasBody;
import com.artipie.http.hm.RsHasHeaders;
import com.artipie.http.hm.RsHasStatus;
import com.artipie.http.rs.RsStatus;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link RsError}.
 * @since 0.1
 * @checkstyle MagicNumberCheck (500 lines)
 */
class RsErrorTest {

    @Test
    void responseWithOkStatusJsonBodyAndHeader() {
        final List<String> inp = new LinkedList<>();
        inp.add("first message");
        inp.add("second message");
        MatcherAssert.assertThat(
            new RsError(inp),
            Matchers.allOf(
                new RsHasStatus(RsStatus.OK),
                new RsHasHeaders(
                    new ContentType("application/json; charset=UTF-8"),
                    new ContentLength(67)
                ),
                // @checkstyle LineLengthCheck (1 line)
                new RsHasBody("{\"errors\":[{\"detail\":\"first message\"},{\"detail\":\"second message\"}]}".getBytes(StandardCharsets.UTF_8))
            )
        );
    }

}
