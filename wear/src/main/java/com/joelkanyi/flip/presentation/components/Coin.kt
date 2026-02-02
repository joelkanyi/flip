package com.joelkanyi.flip.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import com.joelkanyi.flip.data.model.CoinSide
import com.joelkanyi.flip.domain.model.CoinState

@Composable
fun Coin(
    state: CoinState,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

    // Main rotation around X axis (the flip rotation - like a somersault)
    val rotationX = remember { Animatable(0f) }
    // Slight Z rotation for wobble
    val rotationZ = remember { Animatable(0f) }
    // Scale for anticipation and bounce
    val scale = remember { Animatable(1f) }
    // Vertical movement for toss effect
    val translationY = remember { Animatable(0f) }

    // Idle floating animation
    val infiniteTransition = rememberInfiniteTransition(label = "idle")
    val idleFloat by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "idleFloat"
    )

    // Custom easing for realistic deceleration
    val decelerateEasing = CubicBezierEasing(0.0f, 0.7f, 0.3f, 1.0f)

    LaunchedEffect(state) {
        when (state) {
            is CoinState.Idle -> {
                rotationX.animateTo(0f, tween(400, easing = FastOutSlowInEasing))
                rotationZ.animateTo(0f, tween(400))
                scale.animateTo(1f, tween(400))
                translationY.animateTo(0f, tween(400))
            }

            is CoinState.Anticipating -> {
                // Wind up - scale down, pull back slightly
                scale.animateTo(0.85f, tween(150, easing = FastOutSlowInEasing))
                rotationX.animateTo(-20f, tween(150))
                translationY.animateTo(10f, tween(150))
            }

            is CoinState.Spinning -> {
                // Launch up and spin!
                // First, spring up
                scale.animateTo(1.1f, tween(200))
                translationY.animateTo(-30f, tween(300, easing = FastOutSlowInEasing))

                // Calculate total rotation - multiple full rotations ending at correct side
                // Heads = 0°, Tails = 180°
                val numRotations = 6 // 6 full rotations for drama
                val baseRotation = numRotations * 360f
                val finalAngle = if (state.targetSide == CoinSide.HEADS) 0f else 180f
                val totalRotation = baseRotation + finalAngle

                // Spin with realistic physics - starts fast, slows down
                rotationX.animateTo(
                    targetValue = totalRotation,
                    animationSpec = tween(
                        durationMillis = 1800, // Longer spin for tension
                        easing = decelerateEasing
                    )
                )
            }

            is CoinState.Decelerating -> {
                translationY.animateTo(0f, tween(400, easing = FastOutSlowInEasing))
                scale.animateTo(1f, tween(300))
            }

            is CoinState.Wobbling -> {
                // Coin lands and wobbles side to side
                rotationZ.animateTo(8f, tween(80))
                rotationZ.animateTo(-6f, tween(100))
                rotationZ.animateTo(4f, tween(80))
                rotationZ.animateTo(-2f, tween(60))
                rotationZ.animateTo(0f, tween(80))
            }

            is CoinState.Settled -> {
                val finalAngle = if (state.result == CoinSide.HEADS) 0f else 180f
                rotationX.snapTo(finalAngle)
                rotationZ.snapTo(0f)
                scale.snapTo(1f)
                translationY.snapTo(0f)
            }
        }
    }

    // Determine which face to show based on rotation
    // The coin shows heads when rotationX is near 0, 360, 720, etc.
    // It shows tails when rotationX is near 180, 540, 900, etc.
    val normalizedRotation = ((rotationX.value % 360f) + 360f) % 360f
    val showHeads = normalizedRotation < 90f || normalizedRotation > 270f

    val visibleSide = if (showHeads) CoinSide.HEADS else CoinSide.TAILS

    // Apply idle float only when resting
    val yOffset = when (state) {
        is CoinState.Idle, is CoinState.Settled -> -idleFloat + translationY.value
        else -> translationY.value
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                this.translationY = yOffset * density.density
                // True 3D rotation around X axis
                this.rotationX = rotationX.value
                this.rotationZ = rotationZ.value
                this.scaleX = scale.value
                this.scaleY = scale.value
                // Important: camera distance for proper 3D perspective
                this.cameraDistance = 20f * density.density
            },
        contentAlignment = Alignment.Center
    ) {
        // Show the appropriate face
        // When the coin is flipped past 90°, we need to mirror the content
        CoinFace(
            side = visibleSide,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    // When showing the back side, flip it so text isn't mirrored
                    if (!showHeads) {
                        this.rotationX = 180f
                    }
                }
        )
    }
}
