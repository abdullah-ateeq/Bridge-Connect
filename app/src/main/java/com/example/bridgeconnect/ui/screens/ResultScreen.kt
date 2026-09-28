package com.example.bridgeconnect.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bridgeconnect.model.BridgeConnectState
import com.example.bridgeconnect.model.Player
import com.example.bridgeconnect.ui.theme.BronzeAccent
import com.example.bridgeconnect.ui.theme.BronzeGrid
import com.example.bridgeconnect.ui.theme.BronzeMuted
import com.example.bridgeconnect.ui.theme.CopperDark
import com.example.bridgeconnect.ui.theme.CopperLight
import com.example.bridgeconnect.ui.theme.CopperPrimary
import com.example.bridgeconnect.ui.theme.MidnightBackground
import com.example.bridgeconnect.ui.theme.SapphireDark
import com.example.bridgeconnect.ui.theme.SapphireLight
import com.example.bridgeconnect.ui.theme.SapphirePrimary
import com.example.bridgeconnect.ui.theme.SlateSurface
import com.example.bridgeconnect.ui.theme.SlateSurfaceVariant
import com.example.bridgeconnect.ui.theme.TextMuted
import com.example.bridgeconnect.ui.theme.TextWhite
import java.util.Locale

@Composable
fun ResultScreen(
    state: BridgeConnectState,
    onPlayAgain: () -> Unit,
    onHomeClicked: () -> Unit
) {
    val scrollState = rememberScrollState()

    val winner = state.winner
    val titleText = when (winner) {
        Player.PLAYER_ONE -> "PLAYER 1 WINS"
        Player.PLAYER_TWO -> "PLAYER 2 WINS"
        null -> "MATCH DRAW"
    }

    val titleColor = when (winner) {
        Player.PLAYER_ONE -> CopperLight
        Player.PLAYER_TWO -> SapphireLight
        null -> BronzeAccent
    }

    val durationText = String.format(
        Locale.US,
        "%02d:%02d",
        state.matchDurationSeconds / 60,
        state.matchDurationSeconds % 60
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Winner Symbol Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        when (winner) {
                            Player.PLAYER_ONE -> CopperDark
                            Player.PLAYER_TWO -> SapphireDark
                            null -> SlateSurfaceVariant
                        },
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (winner) {
                        Player.PLAYER_ONE -> "◆"
                        Player.PLAYER_TWO -> "●"
                        null -> "—"
                    },
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = titleText,
                color = titleColor,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 3.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = when (winner) {
                    Player.PLAYER_ONE -> "Continuous bridge path connected Top ↔ Bottom"
                    Player.PLAYER_TWO -> "Continuous bridge path connected Left ↔ Right"
                    null -> "Grid full without a completed connection"
                },
                color = TextMuted,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Summary Stats Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BronzeGrid.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "MATCH METRICS",
                        color = BronzeAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )

                    MetricRow("Moves Played", "${state.moveCount}")
                    MetricRow(
                        "Winning Path Nodes",
                        if (state.winningPath.isNotEmpty()) "${state.winningPath.size} nodes" else "N/A"
                    )
                    MetricRow("Total Bridges Created", "${state.totalBridgeCount}")
                    MetricRow(
                        "Player 1 Nodes",
                        "${state.player1NodeCount}",
                        valueColor = CopperPrimary
                    )
                    MetricRow(
                        "Player 2 Nodes",
                        "${state.player2NodeCount}",
                        valueColor = SapphirePrimary
                    )
                    MetricRow("Match Duration", durationText)
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Action Buttons: PLAY AGAIN & HOME
            Button(
                onClick = onPlayAgain,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = androidx.compose.foundation.layout.PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(CopperPrimary, BronzeAccent, SapphirePrimary)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Replay,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PLAY AGAIN",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onHomeClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, BronzeGrid.copy(alpha = 0.8f))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        tint = BronzeMuted,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "HOME",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun MetricRow(
    label: String,
    value: String,
    valueColor: Color = TextWhite
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextMuted, fontSize = 13.sp)
        Text(text = value, color = valueColor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}
