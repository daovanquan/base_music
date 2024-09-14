package com.marusys.auto.music.ui.anim

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import kotlin.random.Random

@Composable
fun WaveformBar(
    modifier: Modifier = Modifier,
    barCount: Int = 5,
    barWidth: Dp = 3.dp,
    barSpacing: Dp = 1.dp,
    color: Color = Color.Cyan,
    isAnimating: Boolean = true
) {

    val heightDivider by animateFloatAsState(
        targetValue = if (isAnimating) 1f else 6f,
        animationSpec = tween(1000, easing = LinearEasing), label = "heightDivider"
    )
    val animations = mutableListOf<State<Float>>()
    val random = remember { Random(System.currentTimeMillis()) }
    val infiniteAnimation = rememberInfiniteTransition(label = "infiniteAnimation")
    repeat(barCount) {
        val durationMillis = random.nextInt(500, 2000)
        animations += infiniteAnimation.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis),
                repeatMode = RepeatMode.Reverse,
            ), label = "animations"
        )
    }
    val initialMultipliers = remember {
        mutableListOf<Float>().apply {
            repeat(barCount) { this += random.nextFloat() }
        }
    }

    Canvas(modifier = modifier) {
        val animatedVolumeWidth = barCount * (barWidth.toPx() + barSpacing.toPx())
        var startOffset = (size.width - animatedVolumeWidth) / 2
        val barMinHeight = 0f
        val canvasHeight = size.height
        val barMaxHeight = canvasHeight / 2f / heightDivider
        val canvasCenterY = size.height / 2
        repeat(barCount) { index ->
            val currentSize = animations[index].value
            var barHeightPercent = initialMultipliers[index] + currentSize
            if (barHeightPercent > 1.0f) {
                val diff = barHeightPercent - 1.0f
                barHeightPercent = 1.0f - diff
            }
            val barHeight = ((barMaxHeight - barMinHeight) * barHeightPercent)
            drawLine(
                color = color,
                start = Offset(startOffset, canvasCenterY - barHeight / 2),
                end = Offset(startOffset, canvasCenterY + barHeight / 2),
                strokeWidth = barWidth.toPx(),
                cap = StrokeCap.Round,
            )
            startOffset += barWidth.toPx() + barSpacing.toPx()
        }
    }
}