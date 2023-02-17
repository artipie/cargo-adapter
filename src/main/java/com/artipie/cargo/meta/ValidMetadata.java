/*
 * The MIT License (MIT) Copyright (c) 2020-2023 artipie.com
 * https://github.com/artipie/cargo-adapter/LICENSE
 */
package com.artipie.cargo.meta;

import com.artipie.ArtipieException;
import java.io.StringReader;
import java.util.regex.Pattern;
import javax.json.Json;
import javax.json.JsonObject;

/**
 * Valid package metadata.
 * @since 0.1
 */
public final class ValidMetadata {

    /**
     * Package name regex.
     */
    private static final Pattern NAME = Pattern.compile("[\\w-_]+");

    /**
     * SemVer 2 regex to check the version, see<br/>
     * https://semver.org/#is-there-a-suggested-regular-expression-regex-to-check-a-semver-string.
     * @checkstyle LineLengthCheck (5 lines)
     */
    private static final Pattern VER = Pattern.compile("^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?$");

    /**
     * Cargo package metadata.
     */
    private final JsonObject meta;

    /**
     * Ctor.
     * @param meta Cargo package metadata
     */
    public ValidMetadata(final JsonObject meta) {
        this.meta = meta;
    }

    /**
     * Ctor.
     * @param meta Cargo package metadata string
     */
    public ValidMetadata(final String meta) {
        this(Json.createReader(new StringReader(meta)).readObject());
    }

    /**
     * Validate and return package name.
     * @return Name
     * @throws ArtipieException If name is not valid
     */
    public String validName() {
        final String name = this.meta.getString("name");
        if (ValidMetadata.NAME.matcher(name).matches()) {
            return name;
        }
        throw new ArtipieException(
            "Package name is illegal, only alphanumerics chars and _ - are allowed"
        );
    }

    /**
     * Validate and return package version. This must be a valid version number
     * according to the Semantic Versioning 2.0.0.
     * @return Version
     * @throws ArtipieException If name is not valid
     */
    public String validVersion() {
        final String vers = this.meta.getString("vers");
        if (ValidMetadata.VER.matcher(vers).matches()) {
            return vers;
        }
        throw new ArtipieException(
            "Package version is illegal,it should correspond to semver 2.0.0"
        );
    }
}
