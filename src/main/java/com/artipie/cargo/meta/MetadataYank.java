/*
 * The MIT License (MIT) Copyright (c) 2020-2021 artipie.com
 * https://github.com/artipie/cargo-adapter/LICENSE
 */
package com.artipie.cargo.meta;

import com.artipie.ArtipieException;
import com.artipie.asto.ArtipieIOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.Optional;
import java.util.function.BiFunction;
import javax.json.Json;
import javax.json.JsonObjectBuilder;

/**
 * Set `yank` field of the package metadata of the given version to
 * true or false.
 * @since 0.1
 */
public final class MetadataYank implements
    BiFunction<Optional<InputStream>, OutputStream, Boolean> {

    /**
     * Package version.
     */
    private final String version;

    /**
     * Value to set to `yank` field.
     */
    private final boolean value;

    /**
     * Ctor.
     * @param version Package version
     * @param value Value to set to `yank` field
     */
    public MetadataYank(final String version, final boolean value) {
        this.version = version;
        this.value = value;
    }

    @Override
    @SuppressWarnings("PMD.AssignmentInOperand")
    public Boolean apply(final Optional<InputStream> input, final OutputStream output) {
        if (!input.isPresent()) {
            throw new ArtipieException("Package metadata does not exists!");
        }
        final String vers = String.format("\"vers\":\"%s\"", this.version);
        try (
            BufferedReader brd = new BufferedReader(new InputStreamReader(input.get()));
            BufferedWriter bwr = new BufferedWriter(new OutputStreamWriter(output))
        ) {
            String line;
            boolean first = true;
            boolean found = false;
            while ((line = brd.readLine()) != null) {
                if (!first) {
                    bwr.newLine();
                }
                first = false;
                if (line.contains(vers)) {
                    found = true;
                    final JsonObjectBuilder obj = Json.createObjectBuilder(
                        Json.createReader(new StringReader(line)).readObject()
                    );
                    obj.add("yank", this.value);
                    line = obj.build().toString();
                }
                bwr.write(line);
            }
            return found;
        } catch (final IOException err) {
            throw new ArtipieIOException(err);
        }
    }
}
