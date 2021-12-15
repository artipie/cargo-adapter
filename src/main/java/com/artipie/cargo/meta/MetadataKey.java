/*
 * The MIT License (MIT) Copyright (c) 2020-2021 artipie.com
 * https://github.com/artipie/cargo-adapter/LICENSE
 */
package com.artipie.cargo.meta;

import com.artipie.asto.Key;

/**
 * Cargo metadata file name by name of the package. The rules are:
 * <ul>
 * <li>Packages with 1 character names are placed in a directory named {@code 1}.</li>
 * <li>Packages with 2 character names are placed in a directory named {@code 2}.</li>
 * <li>Packages with 3 character names are placed in the directory {@code 3/&#123;first-character&#125;},
 * where {@code &#123;first-character&#125;} is the first character of the package name.</li>
 * <li>All other packages are stored in directories named {@code &#123;first-two&#125;/&#123;second-two&#125;} where the top
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
        if (this.pkg.length() == 1) {
            res = new Key.From("1");
        } else if (this.pkg.length() == 2) {
            res = new Key.From("2");
        } else if (this.pkg.length() == 3) {
            res = new Key.From("3", this.pkg.substring(0, 1));
        } else {
            res = new Key.From(this.pkg.substring(0, 2), this.pkg.substring(2, 4));
        }
        return new Key.From(res, this.pkg);
    }
}
