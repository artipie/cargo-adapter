/*
 * The MIT License (MIT) Copyright (c) 2020-2021 artipie.com
 * https://github.com/artipie/cargo-adapter/LICENSE
 */
package com.artipie.cargo.http;

import com.artipie.http.Slice;
import com.artipie.http.rs.StandardRs;
import com.artipie.http.slice.SliceSimple;

/**
 * Main cargo repo entry point.
 * @since 0.1
 */
public final class CargoSlice extends Slice.Wrap {

    /**
     * Ctor.
     */
    public CargoSlice() {
        super(new SliceSimple(StandardRs.OK));
    }
}
