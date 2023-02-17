/*
 * The MIT License (MIT) Copyright (c) 2020-2023 artipie.com
 * https://github.com/artipie/cargo-adapter/LICENSE
 */
package com.artipie.cargo.http;

import com.artipie.asto.Storage;
import com.artipie.http.Slice;
import com.artipie.http.rq.RqMethod;
import com.artipie.http.rt.ByMethodsRule;
import com.artipie.http.rt.RtRule;
import com.artipie.http.rt.RtRulePath;
import com.artipie.http.rt.SliceRoute;
import com.artipie.http.slice.SliceSimple;

/**
 * Main cargo repo entry point.
 * @since 0.1
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class CargoSlice extends Slice.Wrap {

    /**
     * Ctor.
     * @param asto Storage
     */
    public CargoSlice(final Storage asto) {
        super(
            new SliceRoute(
                new RtRulePath(
                    new RtRule.All(
                        new RtRule.ByPath("/api/v1/crates/new"),
                        new ByMethodsRule(RqMethod.PUT)
                    ),
                    new PublishSlice(asto)
                ),
                new RtRulePath(
                    new RtRule.All(
                        new RtRule.ByPath(YankSlice.YANK_PTRN),
                        new ByMethodsRule(RqMethod.DELETE, RqMethod.PUT)
                    ),
                    new YankSlice(asto)
                ),
                new RtRulePath(
                    RtRule.FALLBACK,
                    new SliceSimple(new RsError("Endpoint does not exists"))
                )
            )
        );
    }
}
