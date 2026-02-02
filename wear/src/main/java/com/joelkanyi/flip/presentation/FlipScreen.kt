package com.joelkanyi.flip.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import com.joelkanyi.flip.data.model.CoinSide
import com.joelkanyi.flip.domain.model.CoinState
import com.joelkanyi.flip.presentation.components.Coin
import com.joelkanyi.flip.presentation.components.PulseRings
import com.joelkanyi.flip.presentation.theme.FlipColors
import com.joelkanyi.flip.presentation.viewmodel.CoinFlipViewModel

@Composable
fun FlipScreen(
    viewModel: CoinFlipViewModel,
    onNavigateToStats: () -> Unit
) {
    val coinState by viewModel.coinState.collectAsState()

    val isSettled = coinState is CoinState.Settled
    val isIdle = coinState is CoinState.Idle
    val isAnimating = !isIdle && !isSettled

    // Animated background color
    val backgroundColor by animateColorAsState(
        targetValue = when {
            isSettled -> when ((coinState as CoinState.Settled).result) {
                CoinSide.HEADS -> FlipColors.HeadsBackground
                CoinSide.TAILS -> FlipColors.TailsBackground
            }
            else -> FlipColors.Background
        },
        animationSpec = tween(durationMillis = 500),
        label = "backgroundColor"
    )

    // Result text color
    val resultColor = when {
        isSettled -> when ((coinState as CoinState.Settled).result) {
            CoinSide.HEADS -> FlipColors.HeadsText
            CoinSide.TAILS -> FlipColors.TailsText
        }
        else -> FlipColors.TextMuted
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                viewModel.flip()
            }
    ) {
        // Pulse rings behind coin (visible only when settled)
        if (isSettled) {
            PulseRings(
                result = (coinState as CoinState.Settled).result,
                isVisible = true,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Main content - vertically centered with proper spacing
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Coin - centered and appropriately sized
            Coin(
                state = coinState,
                modifier = Modifier.size(90.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Result text area - fixed height to prevent layout shift
            Box(
                modifier = Modifier.height(44.dp),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isSettled -> {
                        val result = (coinState as CoinState.Settled).result
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = result.name,
                                color = resultColor,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "tap again",
                                color = FlipColors.TextMuted.copy(alpha = 0.6f),
                                fontSize = 10.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    isIdle -> {
                        Text(
                            text = "TAP TO FLIP",
                            color = FlipColors.TextMuted,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                    else -> {
                        // During animation - show nothing (space is reserved)
                    }
                }
            }
        }

        // Stats button - 48dp touch target for accessibility (WO-V2)
        if (!isAnimating) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .size(48.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onNavigateToStats() },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "•••",
                        color = FlipColors.TextMuted.copy(alpha = 0.8f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
