/*
 * The MIT License (MIT) Copyright (c) 2020-2023 artipie.com
 * https://github.com/artipie/cargo-adapter/LICENSE
 */
package com.artipie.cargo.meta;

import com.artipie.asto.ArtipieIOException;
import com.artipie.asto.misc.UncheckedIOConsumer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * Merge new package metadata into optionally existing metadata file.
 * @since 0.1
 */
public final class MetadataMerge implements BiConsumer<Optional<InputStream>, OutputStream> {

    /**
     * String metadata to add.
     */
    private final String add;

    /**
     * Ctor.
     * @param add String metadata to add
     */
    public MetadataMerge(final String add) {
        this.add = add;
    }

    @Override
    @SuppressWarnings("PMD.AssignmentInOperand")
    public void accept(final Optional<InputStream> input, final OutputStream output) {
        try {
            if (input.isPresent()) {
                // @checkstyle MagicNumberCheck (1 line)
                final byte[] buffer = new byte[4 * 1024];
                int len;
                while ((len = input.get().read(buffer)) > 0) {
                    output.write(buffer, 0, len);
                }
                output.write("\n".getBytes(StandardCharsets.UTF_8));
            }
            output.write(this.add.getBytes(StandardCharsets.UTF_8));
        } catch (final IOException err) {
            throw new ArtipieIOException(err);
        } finally {
            Optional.of(output).ifPresent(new UncheckedIOConsumer<>(OutputStream::close));
            input.ifPresent(new UncheckedIOConsumer<>(InputStream::close));
        }
    }
}
