package com.joelkanyi.flip.domain.model

import com.joelkanyi.flip.data.model.CoinSide

sealed class CoinState {
    abstract val displayedSide: CoinSide

    data class Idle(
        override val displayedSide: CoinSide
    ) : CoinState()

    data class Anticipating(
        val startingSide: CoinSide
    ) : CoinState() {
        override val displayedSide: CoinSide = startingSide
    }

    data class Spinning(
        val startSide: CoinSide,
        val targetSide: CoinSide,
        val rotations: Int = 5
    ) : CoinState() {
        override val displayedSide: CoinSide = startSide
    }

    data class Decelerating(
        val targetSide: CoinSide
    ) : CoinState() {
        override val displayedSide: CoinSide = targetSide
    }

    data class Wobbling(
        val targetSide: CoinSide
    ) : CoinState() {
        override val displayedSide: CoinSide = targetSide
    }

    data class Settled(
        val result: CoinSide
    ) : CoinState() {
        override val displayedSide: CoinSide = result
    }
}
