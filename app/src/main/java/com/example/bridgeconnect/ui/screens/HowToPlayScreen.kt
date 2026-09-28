package com.example.bridgeconnect.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bridgeconnect.logic.BridgeRules
import com.example.bridgeconnect.model.BridgeNode
import com.example.bridgeconnect.ui.theme.BoardSlateClassic
import com.example.bridgeconnect.ui.theme.BronzeAccent
import com.example.bridgeconnect.ui.theme.BronzeGrid
import com.example.bridgeconnect.ui.theme.BronzeMuted
import com.example.bridgeconnect.ui.theme.CopperDark
import com.example.bridgeconnect.ui.theme.CopperLight
import com.example.bridgeconnect.ui.theme.CopperPrimary
import com.example.bridgeconnect.ui.theme.CopperRail
import com.example.bridgeconnect.ui.theme.GoldGlow
import com.example.bridgeconnect.ui.theme.MidnightBackground
import com.example.bridgeconnect.ui.theme.SapphireDark
import com.example.bridgeconnect.ui.theme.SapphireLight
import com.example.bridgeconnect.ui.theme.SapphirePrimary
import com.example.bridgeconnect.ui.theme.SapphireRail
import com.example.bridgeconnect.ui.theme.SlateSurface
import com.example.bridgeconnect.ui.theme.SocketFillDark
import com.example.bridgeconnect.ui.theme.SocketStrokeDark
import com.example.bridgeconnect.ui.theme.TextMuted
import com.example.bridgeconnect.ui.theme.TextWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HowToPlayScreen(
    onBackClicked: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "HOW TO PLAY",
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MidnightBackground
                )
            )
        },
        containerColor = MidnightBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Objective Section
            RuleCard(title = "OBJECTIVE") {
                RuleBullet(
                    symbol = "◆",
                    symbolColor = CopperLight,
                    title = "Player 1 (Top ↔ Bottom)",
                    desc = "Connect any node touching the Top edge (Row 0) to any node touching the Bottom edge (Row 6) through a continuous chain of bridge links."
                )

                Spacer(modifier = Modifier.height(10.dp))

                RuleBullet(
                    symbol = "●",
                    symbolColor = SapphireLight,
                    title = "Player 2 (Left ↔ Right)",
                    desc = "Connect any node touching the Left edge (Col 0) to any node touching the Right edge (Col 6) through a continuous chain of bridge links."
                )
            }

            // Connection Rule with Visual Mini-Canvas
            RuleCard(title = "THE BRIDGE CONNECTION RULE") {
                Text(
                    text = "Nodes connect automatically if they belong to the same player and are separated by a knight-like offset: (±2, ±1) or (±1, ±2).",
                    color = TextWhite,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Visual Diagram of Knight Offsets from Center Node (2, 2)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.2f),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val pad = 24.dp.toPx()
                        val step = (minOf(w, h) - 2 * pad) / 4f
                        val centerOffset = Offset(w / 2f, h / 2f)

                        // Draw miniature 5x5 grid
                        drawRoundRect(
                            color = BoardSlateClassic,
                            size = Size(w, h),
                            cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx())
                        )
                        drawRoundRect(
                            color = BronzeGrid,
                            size = Size(w, h),
                            cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx()),
                            style = Stroke(1.dp.toPx())
                        )

                        // Center node is at (2, 2)
                        val centerR = 2
                        val centerC = 2

                        // Draw sockets for 5x5
                        for (r in 0..4) {
                            for (c in 0..4) {
                                val pt = Offset(
                                    x = centerOffset.x + (c - centerC) * step,
                                    y = centerOffset.y + (r - centerR) * step
                                )
                                drawCircle(
                                    color = SocketFillDark,
                                    radius = step * 0.22f,
                                    center = pt
                                )
                                drawCircle(
                                    color = SocketStrokeDark,
                                    radius = step * 0.22f,
                                    center = pt,
                                    style = Stroke(1.dp.toPx())
                                )
                            }
                        }

                        // Allowed offsets from center (2, 2)
                        for ((dr, dc) in BridgeRules.bridgeOffsets) {
                            val targetPt = Offset(
                                x = centerOffset.x + dc * step,
                                y = centerOffset.y + dr * step
                            )

                            // Bridge line
                            drawLine(
                                color = BronzeAccent.copy(alpha = 0.7f),
                                start = centerOffset,
                                end = targetPt,
                                strokeWidth = 2.dp.toPx(),
                                cap = StrokeCap.Round
                            )

                            // Target socket highlight
                            drawCircle(
                                color = GoldGlow.copy(alpha = 0.85f),
                                radius = step * 0.24f,
                                center = targetPt,
                                style = Stroke(1.5.dp.toPx())
                            )
                        }

                        // Center Placed Node
                        drawCircle(
                            color = CopperDark,
                            radius = step * 0.3f,
                            center = centerOffset
                        )
                        drawCircle(
                            color = CopperPrimary,
                            radius = step * 0.26f,
                            center = centerOffset
                        )
                        // Diamond
                        val path = Path().apply {
                            val s = step * 0.12f
                            moveTo(centerOffset.x, centerOffset.y - s)
                            lineTo(centerOffset.x + s, centerOffset.y)
                            lineTo(centerOffset.x, centerOffset.y + s)
                            lineTo(centerOffset.x - s, centerOffset.y)
                            close()
                        }
                        drawPath(path, Color.White, style = Fill)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Center node automatically links to all 8 highlighted positions if owned by the same player.",
                    color = BronzeAccent,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }

            // Key Principles
            RuleCard(title = "CORE PRINCIPLES") {
                RulePrinciple(
                    number = "1",
                    title = "Automatic Bridges",
                    description = "Placing a node automatically forms all valid bridge links. No manual bridge placing is required."
                )

                RulePrinciple(
                    number = "2",
                    title = "No Captures or Removals",
                    description = "Claimed nodes remain permanently on the 7×7 board. Bridges cannot be destroyed."
                )

                RulePrinciple(
                    number = "3",
                    title = "Visual Crossing Permitted",
                    description = "Bridges may visually cross each other diagonally. Crossings do not interact or block connections."
                )

                RulePrinciple(
                    number = "4",
                    title = "Continuous Network Wins",
                    description = "The first player whose connected bridge component reaches both opposite sides wins instantly."
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun RuleCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SlateSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BronzeGrid.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = title,
                color = BronzeAccent,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun RuleBullet(
    symbol: String,
    symbolColor: Color,
    title: String,
    desc: String
) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(SlateSurface, CircleShape)
                .border(1.dp, symbolColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = symbol,
                color = symbolColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                color = TextWhite,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                color = TextMuted,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )
        }
    }
}

@Composable
private fun RulePrinciple(
    number: String,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .background(BronzeGrid.copy(alpha = 0.3f), CircleShape)
                .border(0.8.dp, BronzeAccent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                color = BronzeAccent,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                color = TextWhite,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                color = TextMuted,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
    }
}
