package com.joelkanyi.flip.data.model

import java.security.SecureRandom

enum class CoinSide {
    HEADS,
    TAILS;

    companion object {
        // SecureRandom uses OS entropy sources for cryptographically secure randomness
        // This ensures truly unbiased, unpredictable results
        private val secureRandom = SecureRandom()

        /**
         * Returns a cryptographically secure random coin side.
         * Uses SecureRandom which sources entropy from the OS,
         * making it suitable for fair, unbiased coin flips.
         */
        fun random(): CoinSide = if (secureRandom.nextBoolean()) HEADS else TAILS
    }
}
