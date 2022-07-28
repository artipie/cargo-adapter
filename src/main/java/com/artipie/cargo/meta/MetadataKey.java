/*
 * The MIT License (MIT) Copyright (c) 2020-2022 artipie.com
 * https://github.com/artipie/cargo-adapter/LICENSE
 */
package com.artipie.cargo.meta;

import com.artipie.asto.Key;

/**
 * Cargo metadata file name by name of the package. The rules are:
 * <ul>
 * <li>Packages with 1 character names are placed in a directory named {@code 1}.</li>
 * <li>Packages with 2 character names are placed in a directory named {@code 2}.</li>
 * <li>Packages with 3 character names are placed in the directory
 * {@code 3/first-character}, where {@code first-character} is the first
 * character of the package name.</li>
 * <li>All other packages are stored in directories named
 * {@code first-two/second-two} where the top
 * directory is the first two characters of the package name, and the next subdirectory is the
 * third and fourth characters of the package name. For example, cargo would be stored in a file
 * named {@code ca/rg/cargo}.</li>
 * </ul>
 * The name of the file is package name.
 * For more details check <a href="https://doc.rust-lang.org/cargo/reference/registries.html#index-format">docs</a>.
 * @since 0.1
 */
public final class MetadataKey {

    /**
     * Package name.
     */
    private final String pkg;

    /**
     * Ctor.
     * @param pkg Package name
     */
    public MetadataKey(final String pkg) {
        this.pkg = pkg;
    }

    /**
     * Get package metadata file key.
     * @return Metadata key
     * @checkstyle MagicNumberCheck (20 lines)
     */
    public Key get() {
        final Key res;
        switch (this.pkg.length()) {
            case 1:
                res = new Key.From("1");
                break;
            case 2:
                res = new Key.From("2");
                break;
            case 3:
                res = new Key.From("3", this.pkg.substring(0, 1));
                break;
            default:
                res = new Key.From(this.pkg.substring(0, 2), this.pkg.substring(2, 4));
                break;
        }
        return new Key.From(res, this.pkg);
    }
}
