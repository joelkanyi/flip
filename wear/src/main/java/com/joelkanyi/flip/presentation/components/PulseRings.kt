package com.joelkanyi.flip.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import com.joelkanyi.flip.data.model.CoinSide
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PulseRings(
    result: CoinSide,
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    val ringCount = 2
    val ringAnimations = remember { List(ringCount) { Animatable(0f) } }
    val alphaAnimations = remember { List(ringCount) { Animatable(0f) } }

    val color = when (result) {
        CoinSide.HEADS -> Color(0xFF42A5F5)
        CoinSide.TAILS -> Color(0xFFFFCA28)
    }

    LaunchedEffect(isVisible, result) {
        if (isVisible) {
            // Reset animations
            ringAnimations.forEach { it.snapTo(0f) }
            alphaAnimations.forEach { it.snapTo(0.4f) }

            // Launch staggered ring animations
            ringAnimations.forEachIndexed { index, animation ->
                launch {
                    delay(index * 80L)
                    launch {
                        animation.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(
                                durationMillis = 500,
                                easing = FastOutSlowInEasing
                            )
                        )
                    }
                    launch {
                        alphaAnimations[index].animateTo(
                            targetValue = 0f,
                            animationSpec = tween(
                                durationMillis = 500,
                                easing = FastOutSlowInEasing
                            )
                        )
                    }
                }
            }
        }
    }

    if (isVisible) {
        Canvas(modifier = modifier.fillMaxSize()) {
            val center = this.center
            val startRadius = 50f
            val maxRadius = size.minDimension * 0.45f

            ringAnimations.forEachIndexed { index, animation ->
                val progress = animation.value
                val alpha = alphaAnimations[index].value
                val radius = startRadius + (maxRadius - startRadius) * progress

                drawCircle(
                    color = color.copy(alpha = alpha),
                    radius = radius,
                    center = center,
                    style = Stroke(width = 3f * (1f - progress * 0.5f))
                )
            }
        }
    }
}
