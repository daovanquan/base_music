package com.marusys.auto.music.ui.radio

import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.Region
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.roundToIntRect
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toRegion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.*

@Preview(showBackground = true)
@Composable
fun PreviewCircularLazyList() {
    Column {

        val haptic = LocalHapticFeedback.current

        var sweepAngle by remember {
            mutableStateOf(0f)
        }

        var velocity by remember { mutableStateOf(0f) }
        var dragVelocity by remember { mutableStateOf(0f) }
        val deceleration = 0.96f

        val textBoundingBoxes = remember { mutableStateListOf<Rect>() }
        val textBoxes = remember { mutableStateListOf<RectF>() }
        val textRegions = remember { mutableStateListOf<Pair<Region, Float>>() }

        val scope = rememberCoroutineScope()
        val sweepAngleAnimatable = remember { Animatable(0f) }

        // Apply velocity effect on sweep angle after drag ends
        LaunchedEffect(velocity) {
            if (abs(velocity) > 0.1f) {
                sweepAngle += velocity
                velocity *= deceleration
                sweepAngle = sweepAngle.coerceIn(-360f, 720f)
            }
        }

        LaunchedEffect(((sweepAngle / 2f).roundToInt() * 2).toFloat()) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }

        Canvas(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(0.5f, matchHeightConstraintsFirst = true)
                .background(Color.Black)
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onDragEnd = {
                            sweepAngle = ((sweepAngle / 2f).roundToInt() * 2).toFloat()
                            velocity = dragVelocity * deceleration
                        },
                        onDragStart = {
                            velocity = 0f
                            scope.launch {
                                sweepAngleAnimatable.stop()
                            }
                        }
                    ) { change, dragAmount ->
                        // Handle the swipe gesture by adjusting the sweepAngle
                        sweepAngle -= dragAmount / 10f
                        dragVelocity = -dragAmount / 20f
                        // Clamp the angle to a valid range (0 to 360 degrees)
                        sweepAngle = sweepAngle.coerceIn(-360f, 720f)
                        change.consume()
                    }


                }.pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            velocity = 0f
                            scope.launch {
                                sweepAngleAnimatable.stop()
                            }
                        },
                        onTap =  { offset ->
                        handleTextClick(offset, textRegions) { targetAngle ->
                            scope.launch {
                                withContext(Dispatchers.Default) {
                                    sweepAngleAnimatable.snapTo(sweepAngle)
                                }
                                sweepAngleAnimatable.animateTo(
                                    targetValue = targetAngle,
                                    animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
                                ) {
                                    sweepAngle = sweepAngleAnimatable.value
                                }
                            }
                        }
                    })
                }
                .clip(RectangleShape)
        ) {
            val canvasWidth = size.height
            val center = Offset(
                x = canvasWidth / 1f,
                y = size.height / 2
            )
            textBoundingBoxes.clear()
            textBoxes.clear()
            textRegions.clear()

            val strokeWidth = 2.dp.toPx()
            for (i in 0..360 step 2) {
                val currentAngle = (((i.toFloat() + sweepAngle) / 2f).roundToInt() * 2).toFloat()
                val isHighlighted = (currentAngle % 360).roundToInt() == 0

                val lineStrokeWidth = if (isHighlighted) 4.dp.toPx() else strokeWidth
                val lineColor = if (isHighlighted) Color.Cyan else Color.White

                rotate(i.toFloat() + sweepAngle, pivot = center) {
                    drawLine(
                        color = lineColor,
                        strokeWidth = lineStrokeWidth,
                        start = Offset(
                            x = 0f,
                            y = center.y
                        ),
                        end = Offset(
                            x = 10.dp.toPx(),
                            y = center.y
                        ),
                        cap = StrokeCap.Round
                    )

                    if(i % 20 == 0) {
                        val text = "Radio (${i})"
                        val textPaint = Paint().asFrameworkPaint().apply {
                            isAntiAlias = true
                            textSize = 20.sp.toPx()
                            textAlign = android.graphics.Paint.Align.LEFT
                            color = if (isHighlighted) android.graphics.Color.CYAN else android.graphics.Color.WHITE
                            typeface = android.graphics.Typeface.create(
                                android.graphics.Typeface.DEFAULT,
                                android.graphics.Typeface.BOLD
                            )
                        }
                        // Calculate bounding box for text
                        val textWidth = textPaint.measureText(text)
                        val textHeight = textPaint.textSize
                        val textBounds = Rect(
                            left = canvasWidth * 0.04f,
                            top = center.y - textHeight / 2,
                            right = canvasWidth * 0.04f + textWidth,
                            bottom = center.y + textHeight / 2
                        )
                        textBoundingBoxes.add(textBounds)
                        val path = Path().asAndroidPath()
                        path.addRect(textBounds.toAndroidRectF(), android.graphics.Path.Direction.CW)
                        val matrix = Matrix().apply {
                        }
                        matrix.postRotate((i.toFloat() + sweepAngle), center.x, center.y)
                        path.transform(matrix)
                        val rect = RectF()
                        val region = Region().apply {
                            path.computeBounds(rect, true)

                            textBoxes.add(rect)
                            setPath(path, rect.toRegion())
                        }
                        if(rect.left <= size.width) {
                            textRegions.add(region to (i.toFloat()))

                            drawContext.canvas.nativeCanvas.drawText(
                                text,
                                canvasWidth * 0.04f,
                                center.y + textHeight / 2,
                                textPaint
                            )
                        }
                    }
                }
            }
            textRegions.forEach { region ->
                region.first.boundaryPath?.let {
                    drawPath(it.asComposePath(), Color.Red, style = Stroke())
                }
            }
        }
    }
}

// Function to handle text clicks
private fun handleTextClick(
    offset: Offset,
    regionBoxes: List<Pair<Region, Float>>,
    onTargetFound: (Float) -> Unit = {}
) {
    // Check if the touch point is within any text bounding box
    for (box in regionBoxes) {
        if (box.first.contains(offset.x.toInt(), offset.y.toInt())) {
            // Handle the click on the text
            println("Text clicked!")
            onTargetFound(-box.second)
            return;
        }
    }
}