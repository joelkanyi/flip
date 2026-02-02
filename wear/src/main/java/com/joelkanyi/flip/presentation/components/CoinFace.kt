package com.joelkanyi.flip.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import com.joelkanyi.flip.data.model.CoinSide

@Composable
fun CoinFace(
    side: CoinSide,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2

        when (side) {
            CoinSide.HEADS -> drawHeadsCoin(center, radius, textMeasurer)
            CoinSide.TAILS -> drawTailsCoin(center, radius, textMeasurer)
        }
    }
}

private fun DrawScope.drawHeadsCoin(
    center: Offset,
    radius: Float,
    textMeasurer: androidx.compose.ui.text.TextMeasurer
) {
    // Outer rim - darker blue
    drawCircle(
        color = Color(0xFF1565C0),
        radius = radius,
        center = center
    )

    // Main coin face - metallic blue gradient
    val mainGradient = Brush.radialGradient(
        colors = listOf(
            Color(0xFF90CAF9),  // Light blue highlight
            Color(0xFF42A5F5),  // Medium blue
            Color(0xFF1E88E5),  // Primary blue
            Color(0xFF1565C0)   // Darker edge
        ),
        center = center.copy(x = center.x - radius * 0.2f, y = center.y - radius * 0.2f),
        radius = radius * 1.2f
    )
    drawCircle(
        brush = mainGradient,
        radius = radius * 0.92f,
        center = center
    )

    // Inner raised area
    val innerGradient = Brush.radialGradient(
        colors = listOf(
            Color(0xFF64B5F6),
            Color(0xFF42A5F5),
            Color(0xFF1E88E5)
        ),
        center = center.copy(x = center.x - radius * 0.15f, y = center.y - radius * 0.15f),
        radius = radius * 0.8f
    )
    drawCircle(
        brush = innerGradient,
        radius = radius * 0.75f,
        center = center
    )

    // Decorative ring
    drawCircle(
        color = Color(0xFF90CAF9).copy(alpha = 0.5f),
        radius = radius * 0.78f,
        center = center,
        style = Stroke(width = radius * 0.02f)
    )

    // "H" letter in center - simple and clean
    val letterStyle = TextStyle(
        fontSize = (radius * 0.6f).toSp(),
        fontWeight = FontWeight.Bold,
        color = Color.White
    )
    val letterLayout = textMeasurer.measure("H", letterStyle)
    drawText(
        textLayoutResult = letterLayout,
        topLeft = Offset(
            x = center.x - letterLayout.size.width / 2,
            y = center.y - letterLayout.size.height / 2 - radius * 0.05f
        )
    )

    // Highlight arc at top
    drawArc(
        brush = Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.4f),
                Color.Transparent
            )
        ),
        startAngle = 200f,
        sweepAngle = 80f,
        useCenter = false,
        topLeft = Offset(center.x - radius * 0.6f, center.y - radius * 0.6f),
        size = androidx.compose.ui.geometry.Size(radius * 1.2f, radius * 0.6f),
        style = Stroke(width = radius * 0.08f)
    )
}

private fun DrawScope.drawTailsCoin(
    center: Offset,
    radius: Float,
    textMeasurer: androidx.compose.ui.text.TextMeasurer
) {
    // Outer rim - darker gold
    drawCircle(
        color = Color(0xFFE65100),
        radius = radius,
        center = center
    )

    // Main coin face - metallic gold gradient
    val mainGradient = Brush.radialGradient(
        colors = listOf(
            Color(0xFFFFE082),  // Light gold highlight
            Color(0xFFFFCA28),  // Medium gold
            Color(0xFFFFA000),  // Primary gold
            Color(0xFFE65100)   // Darker edge
        ),
        center = center.copy(x = center.x - radius * 0.2f, y = center.y - radius * 0.2f),
        radius = radius * 1.2f
    )
    drawCircle(
        brush = mainGradient,
        radius = radius * 0.92f,
        center = center
    )

    // Inner raised area
    val innerGradient = Brush.radialGradient(
        colors = listOf(
            Color(0xFFFFD54F),
            Color(0xFFFFCA28),
            Color(0xFFFFA000)
        ),
        center = center.copy(x = center.x - radius * 0.15f, y = center.y - radius * 0.15f),
        radius = radius * 0.8f
    )
    drawCircle(
        brush = innerGradient,
        radius = radius * 0.75f,
        center = center
    )

    // Decorative ring
    drawCircle(
        color = Color(0xFFFFE082).copy(alpha = 0.5f),
        radius = radius * 0.78f,
        center = center,
        style = Stroke(width = radius * 0.02f)
    )

    // "T" letter in center - simple and clean
    val letterStyle = TextStyle(
        fontSize = (radius * 0.6f).toSp(),
        fontWeight = FontWeight.Bold,
        color = Color.White
    )
    val letterLayout = textMeasurer.measure("T", letterStyle)
    drawText(
        textLayoutResult = letterLayout,
        topLeft = Offset(
            x = center.x - letterLayout.size.width / 2,
            y = center.y - letterLayout.size.height / 2 - radius * 0.05f
        )
    )

    // Highlight arc at top
    drawArc(
        brush = Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.4f),
                Color.Transparent
            )
        ),
        startAngle = 200f,
        sweepAngle = 80f,
        useCenter = false,
        topLeft = Offset(center.x - radius * 0.6f, center.y - radius * 0.6f),
        size = androidx.compose.ui.geometry.Size(radius * 1.2f, radius * 0.6f),
        style = Stroke(width = radius * 0.08f)
    )
}

private fun Float.toSp(): androidx.compose.ui.unit.TextUnit = (this / 3f).sp
