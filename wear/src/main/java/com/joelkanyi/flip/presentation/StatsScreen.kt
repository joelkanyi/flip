package com.joelkanyi.flip.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import com.joelkanyi.flip.presentation.theme.FlipColors
import com.joelkanyi.flip.presentation.viewmodel.CoinFlipViewModel

@Composable
fun StatsScreen(
    viewModel: CoinFlipViewModel,
    onNavigateBack: () -> Unit
) {
    val stats by viewModel.stats.collectAsState()
    val listState = rememberScalingLazyListState()

    Scaffold(
        timeText = { TimeText() },
        vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) },
        positionIndicator = { PositionIndicator(scalingLazyListState = listState) }
    ) {
        ScalingLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(FlipColors.Background),
            state = listState,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Title
            item {
                Text(
                    text = "Statistics",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = FlipColors.TextPrimary,
                    modifier = Modifier.padding(top = 24.dp)
                )
            }

            // Total flips - large and prominent
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "${stats.totalFlips}",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = FlipColors.TextPrimary
                    )
                    Text(
                        text = "TOTAL FLIPS",
                        fontSize = 10.sp,
                        color = FlipColors.TextMuted
                    )
                }
            }

            // Heads row
            item {
                StatRow(
                    label = "Heads",
                    count = stats.headsCount,
                    percentage = stats.headsPercentage,
                    color = FlipColors.HeadsMedium,
                    indicatorColor = FlipColors.HeadsMedium
                )
            }

            // Tails row
            item {
                StatRow(
                    label = "Tails",
                    count = stats.tailsCount,
                    percentage = stats.tailsPercentage,
                    color = FlipColors.TailsMedium,
                    indicatorColor = FlipColors.TailsMedium
                )
            }

            // Reset button
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Chip(
                    onClick = {
                        viewModel.resetStats()
                    },
                    label = {
                        Text(
                            text = "Reset Stats",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    colors = ChipDefaults.chipColors(
                        backgroundColor = FlipColors.TailsDark.copy(alpha = 0.3f),
                        contentColor = FlipColors.TailsLight
                    ),
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
            }

            // Done button
            item {
                Button(
                    onClick = onNavigateBack,
                    colors = ButtonDefaults.secondaryButtonColors(),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text("Done")
                }
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun StatRow(
    label: String,
    count: Int,
    percentage: Float,
    color: Color,
    indicatorColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Small coin indicator
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(color)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    fontSize = 14.sp,
                    color = color,
                    fontWeight = FontWeight.Medium
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$count",
                    fontSize = 14.sp,
                    color = FlipColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${(percentage * 100).toInt()}%",
                    fontSize = 12.sp,
                    color = FlipColors.TextMuted
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        ProgressBar(
            progress = percentage,
            color = indicatorColor,
            trackColor = indicatorColor.copy(alpha = 0.2f),
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
        )
    }
}

@Composable
private fun ProgressBar(
    progress: Float,
    color: Color,
    trackColor: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val cornerRadius = CornerRadius(size.height / 2, size.height / 2)

        // Draw track
        drawRoundRect(
            color = trackColor,
            size = size,
            cornerRadius = cornerRadius
        )

        // Draw progress
        if (progress > 0f) {
            drawRoundRect(
                color = color,
                size = Size(size.width * progress.coerceIn(0f, 1f), size.height),
                cornerRadius = cornerRadius
            )
        }
    }
}
