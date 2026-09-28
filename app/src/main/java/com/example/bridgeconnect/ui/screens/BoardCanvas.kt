package com.example.bridgeconnect.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.bridgeconnect.data.BoardThemeStyle
import com.example.bridgeconnect.data.UserSettings
import com.example.bridgeconnect.model.BridgeConnectState
import com.example.bridgeconnect.model.BridgeEdge
import com.example.bridgeconnect.model.BridgeNode
import com.example.bridgeconnect.model.Player
import com.example.bridgeconnect.ui.theme.BoardOledMidnight
import com.example.bridgeconnect.ui.theme.BoardSlateClassic
import com.example.bridgeconnect.ui.theme.BronzeAccent
import com.example.bridgeconnect.ui.theme.BronzeGrid
import com.example.bridgeconnect.ui.theme.CopperDark
import com.example.bridgeconnect.ui.theme.CopperLight
import com.example.bridgeconnect.ui.theme.CopperPrimary
import com.example.bridgeconnect.ui.theme.CopperRail
import com.example.bridgeconnect.ui.theme.GoldGlow
import com.example.bridgeconnect.ui.theme.SapphireDark
import com.example.bridgeconnect.ui.theme.SapphireLight
import com.example.bridgeconnect.ui.theme.SapphirePrimary
import com.example.bridgeconnect.ui.theme.SapphireRail
import com.example.bridgeconnect.ui.theme.SocketFillDark
import com.example.bridgeconnect.ui.theme.SocketStrokeDark
import kotlin.math.hypot

@Composable
fun BoardCanvas(
    state: BridgeConnectState,
    settings: UserSettings,
    onNodeClicked: (BridgeNode) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

    // Placement and bridge draw animation progress (0f -> 1f)
    val placementProgress = remember { Animatable(1f) }
    LaunchedEffect(state.lastMove, state.moveCount) {
        if (state.lastMove != null) {
            placementProgress.snapTo(0f)
            placementProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing)
            )
        }
    }

    // Winning path pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "winPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val boardBg = when (settings.boardTheme) {
        BoardThemeStyle.CLASSIC -> BoardSlateClassic
        BoardThemeStyle.MIDNIGHT -> BoardOledMidnight
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .semantics {
                contentDescription = "7 by 7 Bridge Connect Strategy Board. " +
                        "Player 1 connects Top to Bottom. Player 2 connects Left to Right. " +
                        "Current turn: ${state.currentPlayer.displayName}."
            }
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(state.isGameOver, state.isAnimating) {
                    if (!state.isGameOver && !state.isAnimating) {
                        detectTapGestures { offset ->
                            val boardPadding = size.width * 0.085f
                            val availableWidth = size.width - 2 * boardPadding
                            val step = availableWidth / 6f

                            val c = ((offset.x - boardPadding + step * 0.5f) / step).toInt()
                            val r = ((offset.y - boardPadding + step * 0.5f) / step).toInt()

                            if (r in 0..6 && c in 0..6) {
                                val nodeCenter = Offset(
                                    x = boardPadding + c * step,
                                    y = boardPadding + r * step
                                )
                                val dist = hypot(offset.x - nodeCenter.x, offset.y - nodeCenter.y)
                                if (dist <= step * 0.48f) {
                                    val tappedNode = BridgeNode(r, c)
                                    if (!state.owners.containsKey(tappedNode)) {
                                        onNodeClicked(tappedNode)
                                    }
                                }
                            }
                        }
                    }
                }
        ) {
            val boardWidth = size.width
            val boardHeight = size.height
            val boardPadding = boardWidth * 0.085f
            val availableWidth = boardWidth - 2 * boardPadding
            val step = availableWidth / 6f
            val nodeRadius = step * 0.28f

            fun getNodeCenter(node: BridgeNode): Offset {
                return Offset(
                    x = boardPadding + node.col * step,
                    y = boardPadding + node.row * step
                )
            }

            // ==========================================
            // LAYER 1: Background Board & Outer Rail
            // ==========================================
            val cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx())
            drawRoundRect(
                color = boardBg,
                size = Size(boardWidth, boardHeight),
                cornerRadius = cornerRadius
            )
            // Subtle inner border
            drawRoundRect(
                color = BronzeGrid,
                size = Size(boardWidth, boardHeight),
                cornerRadius = cornerRadius,
                style = Stroke(width = 1.5.dp.toPx())
            )

            // Architectural subtle grid lines
            for (i in 0..6) {
                val x = boardPadding + i * step
                val y = boardPadding + i * step
                // Vertical grid lines
                drawLine(
                    color = BronzeGrid.copy(alpha = 0.25f),
                    start = Offset(x, boardPadding),
                    end = Offset(x, boardHeight - boardPadding),
                    strokeWidth = 1.dp.toPx()
                )
                // Horizontal grid lines
                drawLine(
                    color = BronzeGrid.copy(alpha = 0.25f),
                    start = Offset(boardPadding, y),
                    end = Offset(boardWidth - boardPadding, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            // ==========================================
            // LAYER 2: Target-Side Accents
            // Player 1: Top & Bottom Rails (Copper/Crimson)
            // Player 2: Left & Right Rails (Sapphire/Teal)
            // ==========================================
            val railThickness = 4.dp.toPx()
            val railOffset = boardPadding * 0.4f

            val p1RailAlpha = if (settings.highlightTargetSides) {
                if (state.winner == Player.PLAYER_ONE) pulseAlpha else 0.85f
            } else 0.4f

            val p2RailAlpha = if (settings.highlightTargetSides) {
                if (state.winner == Player.PLAYER_TWO) pulseAlpha else 0.85f
            } else 0.4f

            // Top rail (Player 1)
            drawLine(
                color = CopperRail.copy(alpha = p1RailAlpha),
                start = Offset(boardPadding - 4.dp.toPx(), railOffset),
                end = Offset(boardWidth - boardPadding + 4.dp.toPx(), railOffset),
                strokeWidth = railThickness,
                cap = StrokeCap.Round
            )
            // Bottom rail (Player 1)
            drawLine(
                color = CopperRail.copy(alpha = p1RailAlpha),
                start = Offset(boardPadding - 4.dp.toPx(), boardHeight - railOffset),
                end = Offset(boardWidth - boardPadding + 4.dp.toPx(), boardHeight - railOffset),
                strokeWidth = railThickness,
                cap = StrokeCap.Round
            )

            // Left rail (Player 2)
            drawLine(
                color = SapphireRail.copy(alpha = p2RailAlpha),
                start = Offset(railOffset, boardPadding - 4.dp.toPx()),
                end = Offset(railOffset, boardHeight - boardPadding + 4.dp.toPx()),
                strokeWidth = railThickness,
                cap = StrokeCap.Round
            )
            // Right rail (Player 2)
            drawLine(
                color = SapphireRail.copy(alpha = p2RailAlpha),
                start = Offset(boardWidth - railOffset, boardPadding - 4.dp.toPx()),
                end = Offset(boardWidth - railOffset, boardHeight - boardPadding + 4.dp.toPx()),
                strokeWidth = railThickness,
                cap = StrokeCap.Round
            )

            // ==========================================
            // LAYER 3: Existing Bridge Lines
            // ==========================================
            val hasWinner = state.winner != null
            val winningEdgeSet = state.winningEdges
            val baseBridgeWidth = 2.4.dp.toPx()

            for (bridge in state.bridges) {
                val isWinningEdge = hasWinner && winningEdgeSet.contains(bridge)
                if (isWinningEdge) continue // Rendered in Layer 4 with glow

                val p1 = state.owners[bridge.a]
                val bridgeColor = when (p1) {
                    Player.PLAYER_ONE -> CopperLight
                    Player.PLAYER_TWO -> SapphireLight
                    null -> BronzeAccent
                }

                val cA = getNodeCenter(bridge.a)
                val cB = getNodeCenter(bridge.b)

                val alpha = if (hasWinner) 0.22f else 0.85f

                // If this bridge was newly formed on the last move, animate progressive line draw
                val isNewBridge = state.newlyFormedBridges.contains(bridge)
                if (isNewBridge && placementProgress.value < 1f) {
                    val fromNode = state.lastMove ?: bridge.a
                    val toNode = if (fromNode == bridge.a) bridge.b else bridge.a
                    val fromOffset = getNodeCenter(fromNode)
                    val toOffset = getNodeCenter(toNode)

                    val curEnd = Offset(
                        x = fromOffset.x + (toOffset.x - fromOffset.x) * placementProgress.value,
                        y = fromOffset.y + (toOffset.y - fromOffset.y) * placementProgress.value
                    )
                    drawLine(
                        color = bridgeColor.copy(alpha = alpha),
                        start = fromOffset,
                        end = curEnd,
                        strokeWidth = baseBridgeWidth,
                        cap = StrokeCap.Round
                    )
                } else {
                    drawLine(
                        color = bridgeColor.copy(alpha = alpha),
                        start = cA,
                        end = cB,
                        strokeWidth = baseBridgeWidth,
                        cap = StrokeCap.Round
                    )
                }
            }

            // ==========================================
            // LAYER 4: Winning-Path Highlight
            // ==========================================
            val winner = state.winner
            if (winner != null && state.winningPath.size >= 2) {
                val winColor = when (winner) {
                    Player.PLAYER_ONE -> CopperLight
                    Player.PLAYER_TWO -> SapphireLight
                }

                for (i in 0 until state.winningPath.size - 1) {
                    val pA = getNodeCenter(state.winningPath[i])
                    val pB = getNodeCenter(state.winningPath[i + 1])

                    // Glowing outer aura
                    drawLine(
                        color = GoldGlow.copy(alpha = 0.45f * pulseAlpha),
                        start = pA,
                        end = pB,
                        strokeWidth = 7.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    // Core bright winning line
                    drawLine(
                        color = winColor,
                        start = pA,
                        end = pB,
                        strokeWidth = 3.6.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }

            // ==========================================
            // LAYER 5: Nodes (Sockets & Markers)
            // ==========================================
            val winningNodeSet = if (hasWinner) state.winningPath.toSet() else emptySet()

            for (node in BridgeNode.ALL_NODES) {
                val center = getNodeCenter(node)
                val owner = state.owners[node]
                val isJustPlaced = node == state.lastMove && placementProgress.value < 1f
                val scale = if (isJustPlaced) 0.8f + 0.2f * placementProgress.value else 1.0f
                val r = nodeRadius * scale

                if (owner == null) {
                    // Empty node: recessed socket ring
                    drawCircle(
                        color = SocketFillDark,
                        radius = r * 0.65f,
                        center = center
                    )
                    drawCircle(
                        color = SocketStrokeDark,
                        radius = r * 0.65f,
                        center = center,
                        style = Stroke(width = 1.2.dp.toPx())
                    )
                    // Small center dot for precision targeting
                    drawCircle(
                        color = SocketStrokeDark.copy(alpha = 0.5f),
                        radius = 1.5.dp.toPx(),
                        center = center
                    )
                } else {
                    val isWinningNode = winningNodeSet.contains(node)

                    // Draw winning glow halo
                    if (isWinningNode) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(GoldGlow.copy(alpha = 0.6f * pulseAlpha), Color.Transparent),
                                center = center,
                                radius = r * 2.2f
                            ),
                            radius = r * 2.2f,
                            center = center
                        )
                    }

                    when (owner) {
                        Player.PLAYER_ONE -> {
                            // Player 1: Polished Copper/Crimson Marker with Diamond
                            val primary = if (isWinningNode) CopperLight else CopperPrimary
                            val dark = CopperDark

                            // Subtle drop shadow / outer bevel
                            drawCircle(
                                color = dark.copy(alpha = 0.5f),
                                radius = r + 1.2.dp.toPx(),
                                center = Offset(center.x, center.y + 1.2.dp.toPx())
                            )
                            // Outer body
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(primary, dark),
                                    center = Offset(center.x - r * 0.3f, center.y - r * 0.3f),
                                    radius = r * 1.3f
                                ),
                                radius = r,
                                center = center
                            )
                            // Outer rim
                            drawCircle(
                                color = CopperLight.copy(alpha = 0.7f),
                                radius = r,
                                center = center,
                                style = Stroke(width = 1.2.dp.toPx())
                            )
                            // Distinct internal symbol: Diamond
                            drawDiamondSymbol(center, r * 0.45f, Color.White.copy(alpha = 0.9f))
                        }
                        Player.PLAYER_TWO -> {
                            // Player 2: Polished Sapphire/Teal Marker with Circle
                            val primary = if (isWinningNode) SapphireLight else SapphirePrimary
                            val dark = SapphireDark

                            // Subtle drop shadow / outer bevel
                            drawCircle(
                                color = dark.copy(alpha = 0.5f),
                                radius = r + 1.2.dp.toPx(),
                                center = Offset(center.x, center.y + 1.2.dp.toPx())
                            )
                            // Outer body
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(primary, dark),
                                    center = Offset(center.x - r * 0.3f, center.y - r * 0.3f),
                                    radius = r * 1.3f
                                ),
                                radius = r,
                                center = center
                            )
                            // Outer rim
                            drawCircle(
                                color = SapphireLight.copy(alpha = 0.7f),
                                radius = r,
                                center = center,
                                style = Stroke(width = 1.2.dp.toPx())
                            )
                            // Distinct internal symbol: Concentric ring / circle
                            drawCircle(
                                color = Color.White.copy(alpha = 0.9f),
                                radius = r * 0.38f,
                                center = center,
                                style = Stroke(width = 1.8.dp.toPx())
                            )
                        }
                    }
                }
            }

            // ==========================================
            // LAYER 6: Last-Move Indicator
            // ==========================================
            if (settings.showLastMove && state.lastMove != null) {
                val lastCenter = getNodeCenter(state.lastMove)
                val indicatorRadius = nodeRadius * 1.28f
                // Elegant thin gold/bronze outer ring
                drawCircle(
                    color = GoldGlow.copy(alpha = 0.85f),
                    radius = indicatorRadius,
                    center = lastCenter,
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }
        }
    }
}

private fun DrawScope.drawDiamondSymbol(center: Offset, size: Float, color: Color) {
    val path = Path().apply {
        moveTo(center.x, center.y - size)
        lineTo(center.x + size, center.y)
        lineTo(center.x, center.y + size)
        lineTo(center.x - size, center.y)
        close()
    }
    drawPath(path = path, color = color, style = Fill)
}
