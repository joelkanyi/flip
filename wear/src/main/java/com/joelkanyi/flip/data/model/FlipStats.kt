package com.joelkanyi.flip.data.model

data class FlipStats(
    val headsCount: Int = 0,
    val tailsCount: Int = 0
) {
    val totalFlips: Int get() = headsCount + tailsCount

    val headsPercentage: Float
        get() = if (totalFlips > 0) headsCount.toFloat() / totalFlips else 0f

    val tailsPercentage: Float
        get() = if (totalFlips > 0) tailsCount.toFloat() / totalFlips else 0f
}
