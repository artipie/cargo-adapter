/*
 * The MIT License (MIT) Copyright (c) 2020-2022 artipie.com
 * https://github.com/artipie/cargo-adapter/LICENSE
 */
package com.artipie.cargo;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

/**
 * Authentication tokens.
 * @since 0.1
 */
public interface AuthTokens {

    /**
     * Get valid token item by token string.
     * @param token Token
     * @return Full token info if present and is not expired
     */
    CompletionStage<Optional<TokenItem>> get(String token);

    /**
     * Generates token for username.
     * @param name User name
     * @param ttl Time to live, how long is the token valid
     * @return Token string
     */
    CompletionStage<TokenItem> generate(String name, Duration ttl);

    /**
     * Remove token.
     * @param token Token string
     * @return Completable action, true is valid token found and removed, false otherwise
     */
    CompletionStage<Boolean> remove(String token);

    /**
     * Token item: username, token and expiration day.
     * @since 0.5
     */
    final class TokenItem {

        /**
         * Name of the user.
         */
        private final String uname;

        /**
         * Token.
         */
        private final String tkn;

        /**
         * Expiration date.
         */
        private final Instant expire;

        /**
         * Ctor.
         * @param token Token
         * @param uname Name of the user
         * @param expire Expiration date
         */
        public TokenItem(final String token, final String uname, final Instant expire) {
            this.tkn = token;
            this.uname = uname;
            this.expire = expire;
        }

        /**
         * Name of the user, token owner.
         * @return User name
         */
        public String userName() {
            return this.uname;
        }

        /**
         * Is this token expired?
         * @return True if yes
         */
        public boolean expired() {
            return this.expire.compareTo(Instant.now()) < 0;
        }

        /**
         * Token string value.
         * @return Token
         */
        public String token() {
            return this.tkn;
        }

        @Override
        public boolean equals(final Object other) {
            final boolean res;
            if (this == other) {
                res = true;
            } else if (other == null || this.getClass() != other.getClass()) {
                res = false;
            } else {
                final TokenItem item = (TokenItem) other;
                res = this.uname.equals(item.uname)
                    && this.tkn.equals(item.tkn)
                    && this.expire.equals(item.expire);
            }
            return res;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.tkn, this.uname, this.expire);
        }
    }
}
