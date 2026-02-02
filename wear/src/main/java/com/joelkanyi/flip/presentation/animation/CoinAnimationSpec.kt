package com.joelkanyi.flip.presentation.animation

object CoinAnimationSpec {
    // Phase durations in milliseconds - designed for tension building
    const val IDLE_FLOAT_DURATION = 2000
    const val ANTICIPATION_DURATION = 180      // Wind up
    const val SPIN_DURATION = 2000             // Long dramatic spin
    const val DECELERATION_DURATION = 450      // Coming down
    const val WOBBLE_DURATION = 400            // Landing wobble

    // Total flip time: ~3 seconds for maximum tension

    // Idle floating animation
    const val IDLE_FLOAT_AMPLITUDE = 4f

    // Anticipation pull-back
    const val ANTICIPATION_ROTATION = -20f
    const val ANTICIPATION_SCALE = 0.85f

    // Spin configuration
    const val SPIN_ROTATIONS = 6  // More rotations for drama
    const val TOTAL_SPIN_DEGREES = SPIN_ROTATIONS * 360f

    // Wobble configuration
    const val WOBBLE_AMPLITUDE = 8f

    // Scale values
    const val NORMAL_SCALE = 1f
    const val SPIN_SCALE = 1.1f

    // Camera distance for 3D effect
    const val CAMERA_DISTANCE_MULTIPLIER = 20f
}
