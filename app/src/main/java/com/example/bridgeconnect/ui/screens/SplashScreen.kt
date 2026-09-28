package com.example.bridgeconnect.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bridgeconnect.model.BridgeNode
import com.example.bridgeconnect.ui.theme.BoardSlateClassic
import com.example.bridgeconnect.ui.theme.BronzeAccent
import com.example.bridgeconnect.ui.theme.BronzeGrid
import com.example.bridgeconnect.ui.theme.CopperDark
import com.example.bridgeconnect.ui.theme.CopperLight
import com.example.bridgeconnect.ui.theme.CopperPrimary
import com.example.bridgeconnect.ui.theme.GoldGlow
import com.example.bridgeconnect.ui.theme.MidnightBackground
import com.example.bridgeconnect.ui.theme.SapphireDark
import com.example.bridgeconnect.ui.theme.SapphireLight
import com.example.bridgeconnect.ui.theme.SapphirePrimary
import com.example.bridgeconnect.ui.theme.SocketFillDark
import com.example.bridgeconnect.ui.theme.SocketStrokeDark
import com.example.bridgeconnect.ui.theme.TextMuted
import com.example.bridgeconnect.ui.theme.TextWhite
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    // Animation progress 0f -> 1f over 1500ms
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1500, easing = LinearEasing)
        )
        delay(200)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated Splash Board mini preview
            Box(
                modifier = Modifier.size(240.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val p = progress.value
                    val sizePx = size.width
                    val padding = sizePx * 0.12f
                    val step = (sizePx - 2 * padding) / 4f
                    val r = step * 0.28f

                    // Grid fade (0.0 .. 0.25)
                    val gridAlpha = (p / 0.25f).coerceIn(0f, 1f)

                    // 1. Board background
                    drawRoundRect(
                        color = BoardSlateClassic.copy(alpha = gridAlpha),
                        size = Size(sizePx, sizePx),
                        cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx())
                    )
                    drawRoundRect(
                        color = BronzeGrid.copy(alpha = gridAlpha * 0.4f),
                        size = Size(sizePx, sizePx),
                        cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx()),
                        style = Stroke(width = 1.dp.toPx())
                    )

                    // 2. Sockets
                    for (row in 0..4) {
                        for (col in 0..4) {
                            val center = Offset(padding + col * step, padding + row * step)
                            drawCircle(
                                color = SocketFillDark.copy(alpha = gridAlpha),
                                radius = r * 0.65f,
                                center = center
                            )
                            drawCircle(
                                color = SocketStrokeDark.copy(alpha = gridAlpha * 0.5f),
                                radius = r * 0.65f,
                                center = center,
                                style = Stroke(width = 1.dp.toPx())
                            )
                        }
                    }

                    // Strategic sample nodes forming a network
                    // Player 1 (Copper) sequence: (0, 1) -> (2, 2) -> (4, 3) connects Top to Bottom!
                    // Player 2 (Sapphire) sample: (1, 0) -> (2, 2) -> (3, 4)
                    fun getCoord(row: Int, col: Int) = Offset(padding + col * step, padding + row * step)

                    val p1Chain = listOf(
                        Offset(padding + 1 * step, padding + 0 * step),
                        Offset(padding + 2 * step, padding + 2 * step),
                        Offset(padding + 3 * step, padding + 4 * step)
                    )

                    val p2Chain = listOf(
                        Offset(padding + 0 * step, padding + 3 * step),
                        Offset(padding + 2 * step, padding + 4 * step)
                    )

                    // Node appearance (0.25 .. 0.55)
                    val nodeProgress = ((p - 0.25f) / 0.3f).coerceIn(0f, 1f)
                    if (nodeProgress > 0f) {
                        // Draw Player 2 pieces
                        for (center in p2Chain) {
                            drawCircle(
                                color = SapphireDark.copy(alpha = nodeProgress),
                                radius = r,
                                center = center
                            )
                            drawCircle(
                                color = SapphirePrimary.copy(alpha = nodeProgress),
                                radius = r * 0.85f,
                                center = center
                            )
                        }

                        // Draw Player 1 pieces
                        for (center in p1Chain) {
                            drawCircle(
                                color = CopperDark.copy(alpha = nodeProgress),
                                radius = r,
                                center = center
                            )
                            drawCircle(
                                color = CopperPrimary.copy(alpha = nodeProgress),
                                radius = r * 0.85f,
                                center = center
                            )
                            // Diamond
                            val path = Path().apply {
                                val s = r * 0.4f
                                moveTo(center.x, center.y - s)
                                lineTo(center.x + s, center.y)
                                lineTo(center.x, center.y + s)
                                lineTo(center.x - s, center.y)
                                close()
                            }
                            drawPath(path, Color.White.copy(alpha = nodeProgress * 0.9f), style = Fill)
                        }
                    }

                    // Bridge lines draw (0.55 .. 0.85)
                    val bridgeProgress = ((p - 0.55f) / 0.3f).coerceIn(0f, 1f)
                    if (bridgeProgress > 0f) {
                        // Bridge 1: p1Chain[0] to p1Chain[1]
                        val subProgress1 = (bridgeProgress * 2f).coerceIn(0f, 1f)
                        val end1 = Offset(
                            p1Chain[0].x + (p1Chain[1].x - p1Chain[0].x) * subProgress1,
                            p1Chain[0].y + (p1Chain[1].y - p1Chain[0].y) * subProgress1
                        )
                        drawLine(
                            color = CopperLight.copy(alpha = 0.8f),
                            start = p1Chain[0],
                            end = end1,
                            strokeWidth = 2.5.dp.toPx(),
                            cap = StrokeCap.Round
                        )

                        // Bridge 2: p1Chain[1] to p1Chain[2]
                        val subProgress2 = ((bridgeProgress - 0.5f) * 2f).coerceIn(0f, 1f)
                        if (subProgress2 > 0f) {
                            val end2 = Offset(
                                p1Chain[1].x + (p1Chain[2].x - p1Chain[1].x) * subProgress2,
                                p1Chain[1].y + (p1Chain[2].y - p1Chain[1].y) * subProgress2
                            )
                            drawLine(
                                color = CopperLight.copy(alpha = 0.8f),
                                start = p1Chain[1],
                                end = end2,
                                strokeWidth = 2.5.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        }
                    }

                    // Winning path highlight (0.85 .. 1.0)
                    val winProgress = ((p - 0.85f) / 0.15f).coerceIn(0f, 1f)
                    if (winProgress > 0f) {
                        // Glowing path
                        for (i in 0 until p1Chain.size - 1) {
                            drawLine(
                                color = GoldGlow.copy(alpha = winProgress * 0.6f),
                                start = p1Chain[i],
                                end = p1Chain[i + 1],
                                strokeWidth = 6.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Title appears (0.65 .. 1.0)
            val titleAlpha = ((progress.value - 0.65f) / 0.35f).coerceIn(0f, 1f)
            Text(
                text = "BRIDGE CONNECT",
                color = TextWhite.copy(alpha = titleAlpha),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Build the network. Reach the other side.",
                color = BronzeAccent.copy(alpha = titleAlpha * 0.9f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
