package com.example.bridgeconnect.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bridgeconnect.data.UserSettings
import com.example.bridgeconnect.model.BridgeConnectState
import com.example.bridgeconnect.model.BridgeNode
import com.example.bridgeconnect.model.Player
import com.example.bridgeconnect.ui.theme.BronzeAccent
import com.example.bridgeconnect.ui.theme.BronzeGrid
import com.example.bridgeconnect.ui.theme.BronzeMuted
import com.example.bridgeconnect.ui.theme.CopperDark
import com.example.bridgeconnect.ui.theme.CopperLight
import com.example.bridgeconnect.ui.theme.CopperPrimary
import com.example.bridgeconnect.ui.theme.GoldGlow
import com.example.bridgeconnect.ui.theme.MidnightBackground
import com.example.bridgeconnect.ui.theme.SapphireDark
import com.example.bridgeconnect.ui.theme.SapphireLight
import com.example.bridgeconnect.ui.theme.SapphirePrimary
import com.example.bridgeconnect.ui.theme.SlateSurface
import com.example.bridgeconnect.ui.theme.SlateSurfaceVariant
import com.example.bridgeconnect.ui.theme.TextMuted
import com.example.bridgeconnect.ui.theme.TextWhite

@Composable
fun GameScreen(
    state: BridgeConnectState,
    settings: UserSettings,
    onNodeClicked: (BridgeNode) -> Unit,
    onUndoClicked: () -> Unit,
    onRestartConfirmed: () -> Unit,
    onHomeClicked: () -> Unit,
    onNavigateToResult: () -> Unit
) {
    val hapticFeedback = LocalHapticFeedback.current
    var showRestartDialog by remember { mutableStateOf(false) }

    if (showRestartDialog) {
        AlertDialog(
            onDismissRequest = { showRestartDialog = false },
            title = {
                Text(
                    text = "Restart Match?",
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            },
            text = {
                Text(
                    text = "This will reset the current board and move progress.",
                    color = TextMuted
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showRestartDialog = false
                        onRestartConfirmed()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CopperPrimary)
                ) {
                    Text("Restart", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestartDialog = false }) {
                    Text("Cancel", color = TextMuted)
                }
            },
            containerColor = SlateSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightBackground)
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isLandscape = maxWidth > maxHeight

            if (isLandscape) {
                // Landscape layout
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: Board
                    Box(
                        modifier = Modifier
                            .weight(1.2f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        BoardCanvas(
                            state = state,
                            settings = settings,
                            onNodeClicked = { node ->
                                if (settings.hapticsEnabled) {
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                                onNodeClicked(node)
                            },
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Right: Panels and Controls
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        PlayerStatusCard(
                            player = Player.PLAYER_TWO,
                            nodeCount = state.player2NodeCount,
                            bridgeCount = state.player2BridgeCount,
                            isActive = state.currentPlayer == Player.PLAYER_TWO && !state.isGameOver
                        )

                        TurnBanner(state = state)

                        PlayerStatusCard(
                            player = Player.PLAYER_ONE,
                            nodeCount = state.player1NodeCount,
                            bridgeCount = state.player1BridgeCount,
                            isActive = state.currentPlayer == Player.PLAYER_ONE && !state.isGameOver
                        )

                        ControlsRow(
                            canUndo = state.canUndo && !state.isAnimating && !state.isGameOver,
                            onUndo = onUndoClicked,
                            onRestart = { showRestartDialog = true },
                            onHome = onHomeClicked
                        )
                    }
                }
            } else {
                // Portrait layout
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top Player 2 Panel (Connects Left <-> Right)
                    PlayerStatusCard(
                        player = Player.PLAYER_TWO,
                        nodeCount = state.player2NodeCount,
                        bridgeCount = state.player2BridgeCount,
                        isActive = state.currentPlayer == Player.PLAYER_TWO && !state.isGameOver
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Turn and Objective indicator
                    TurnBanner(state = state)

                    Spacer(modifier = Modifier.height(6.dp))

                    // Large Node Board (Canvas 7x7)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        BoardCanvas(
                            state = state,
                            settings = settings,
                            onNodeClicked = { node ->
                                if (settings.hapticsEnabled) {
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                                onNodeClicked(node)
                            },
                            modifier = Modifier.padding(4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Bottom Player 1 Panel (Connects Top <-> Bottom)
                    PlayerStatusCard(
                        player = Player.PLAYER_ONE,
                        nodeCount = state.player1NodeCount,
                        bridgeCount = state.player1BridgeCount,
                        isActive = state.currentPlayer == Player.PLAYER_ONE && !state.isGameOver
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Compact Controls: Undo, Restart, Home
                    ControlsRow(
                        canUndo = state.canUndo && !state.isAnimating && !state.isGameOver,
                        onUndo = onUndoClicked,
                        onRestart = { showRestartDialog = true },
                        onHome = onHomeClicked
                    )
                }
            }
        }

        // Overlay Banner when Game Over (Win / Draw)
        AnimatedVisibility(
            visible = state.isGameOver,
            enter = fadeIn() + slideInVertically(initialOffsetY = { -it / 2 }),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp)
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface.copy(alpha = 0.95f)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, BronzeAccent),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val title = when (state.winner) {
                        Player.PLAYER_ONE -> "BRIDGE COMPLETE!\nPLAYER 1 WINS"
                        Player.PLAYER_TWO -> "BRIDGE COMPLETE!\nPLAYER 2 WINS"
                        null -> "BOARD FULL\nMATCH DRAW"
                    }
                    val titleColor = when (state.winner) {
                        Player.PLAYER_ONE -> CopperLight
                        Player.PLAYER_TWO -> SapphireLight
                        null -> GoldGlow
                    }

                    Text(
                        text = title,
                        color = titleColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        lineHeight = 26.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (state.winner != null)
                            "Winning connection achieved in ${state.moveCount} moves"
                        else
                            "All 49 nodes claimed without connection",
                        color = TextMuted,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = onNavigateToResult,
                        colors = ButtonDefaults.buttonColors(containerColor = BronzeAccent),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "VIEW MATCH RESULT",
                            color = MidnightBackground,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerStatusCard(
    player: Player,
    nodeCount: Int,
    bridgeCount: Int,
    isActive: Boolean
) {
    val isP1 = player == Player.PLAYER_ONE
    val primaryColor = if (isP1) CopperPrimary else SapphirePrimary
    val darkColor = if (isP1) CopperDark else SapphireDark
    val lightColor = if (isP1) CopperLight else SapphireLight

    val activeBorder = if (isActive) {
        androidx.compose.foundation.BorderStroke(1.5.dp, lightColor)
    } else {
        androidx.compose.foundation.BorderStroke(1.dp, BronzeGrid.copy(alpha = 0.4f))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) SlateSurfaceVariant else SlateSurface
        ),
        border = activeBorder
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Player symbol and name
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(primaryColor, CircleShape)
                        .border(1.dp, lightColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isP1) "◆" else "●",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = player.displayName,
                        color = if (isActive) lightColor else TextWhite,
                        fontSize = 14.sp,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium
                    )
                    Text(
                        text = player.objective,
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
            }

            // Node and Bridge tallies
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$nodeCount",
                        color = TextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Nodes",
                        color = TextMuted,
                        fontSize = 9.sp
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$bridgeCount",
                        color = BronzeAccent,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Bridges",
                        color = TextMuted,
                        fontSize = 9.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TurnBanner(state: BridgeConnectState) {
    val activeColor = when (state.currentPlayer) {
        Player.PLAYER_ONE -> CopperLight
        Player.PLAYER_TWO -> SapphireLight
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(SlateSurface.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
            .border(0.8.dp, BronzeGrid.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(vertical = 5.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        if (!state.isGameOver) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${state.currentPlayer.displayName.uppercase()}'S TURN",
                    color = activeColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "  •  ${state.currentPlayer.objective}",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        } else {
            Text(
                text = "MATCH CONCLUDED",
                color = BronzeAccent,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
private fun ControlsRow(
    canUndo: Boolean,
    onUndo: () -> Unit,
    onRestart: () -> Unit,
    onHome: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onHome,
            modifier = Modifier
                .background(SlateSurface, CircleShape)
                .border(1.dp, BronzeGrid.copy(alpha = 0.6f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Return Home",
                tint = BronzeMuted
            )
        }

        OutlinedButton(
            onClick = onUndo,
            enabled = canUndo,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = TextWhite,
                disabledContentColor = TextMuted.copy(alpha = 0.4f)
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (canUndo) BronzeMuted else BronzeGrid.copy(alpha = 0.3f)
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Undo,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Undo", fontSize = 13.sp)
        }

        IconButton(
            onClick = onRestart,
            modifier = Modifier
                .background(SlateSurface, CircleShape)
                .border(1.dp, BronzeGrid.copy(alpha = 0.6f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Restart Match",
                tint = BronzeMuted
            )
        }
    }
}
