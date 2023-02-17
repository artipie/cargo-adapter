/*
 * The MIT License (MIT) Copyright (c) 2020-2023 artipie.com
 * https://github.com/artipie/cargo-adapter/LICENSE
 */
package com.artipie.cargo.meta;

import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Test for {@link MetadataKey}.
 * @since 0.1
 */
class MetadataKeyTest {

    @ParameterizedTest
    @CsvSource({
        "a,1/a",
        "ab,2/ab",
        "abc,3/a/abc",
        "abcd,ab/cd/abcd",
        "package,pa/ck/package"
    })
    void calculatesKey(final String pkg, final String res) {
        MatcherAssert.assertThat(
            new MetadataKey(pkg).get().string(),
            new IsEqual<>(res)
        );
    }

}
