package com.joelkanyi.flip.data.repository

import com.joelkanyi.flip.data.model.CoinSide
import com.joelkanyi.flip.data.model.FlipStats
import kotlinx.coroutines.flow.Flow

interface FlipRepository {
    val stats: Flow<FlipStats>
    val lastResult: Flow<CoinSide?>

    suspend fun recordFlip(result: CoinSide)
    suspend fun resetStats()
}
