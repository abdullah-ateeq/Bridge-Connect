package com.example.bridgeconnect.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bridgeconnect.data.AppThemeMode
import com.example.bridgeconnect.data.BoardThemeStyle
import com.example.bridgeconnect.data.UserSettings
import com.example.bridgeconnect.ui.theme.BronzeAccent
import com.example.bridgeconnect.ui.theme.BronzeGrid
import com.example.bridgeconnect.ui.theme.BronzeMuted
import com.example.bridgeconnect.ui.theme.CopperPrimary
import com.example.bridgeconnect.ui.theme.MidnightBackground
import com.example.bridgeconnect.ui.theme.SapphirePrimary
import com.example.bridgeconnect.ui.theme.SlateSurface
import com.example.bridgeconnect.ui.theme.SlateSurfaceVariant
import com.example.bridgeconnect.ui.theme.TextMuted
import com.example.bridgeconnect.ui.theme.TextWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settings: UserSettings,
    onThemeModeChanged: (AppThemeMode) -> Unit,
    onBoardThemeChanged: (BoardThemeStyle) -> Unit,
    onSoundChanged: (Boolean) -> Unit,
    onHapticsChanged: (Boolean) -> Unit,
    onShowLastMoveChanged: (Boolean) -> Unit,
    onHighlightTargetSidesChanged: (Boolean) -> Unit,
    onResetStatsConfirmed: () -> Unit,
    onBackClicked: () -> Unit
) {
    val scrollState = rememberScrollState()
    var showResetDialog by remember { mutableStateOf(false) }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Text(
                    text = "Reset Statistics?",
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            },
            text = {
                Text(
                    text = "This will reset all your match records, wins, and shortest path stats back to zero.",
                    color = TextMuted
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showResetDialog = false
                        onResetStatsConfirmed()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CopperPrimary)
                ) {
                    Text("Reset All", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", color = TextMuted)
                }
            },
            containerColor = SlateSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "SETTINGS",
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MidnightBackground)
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
            // Theme Mode
            SettingsSectionCard(title = "APPLICATION THEME") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppThemeMode.entries.forEach { mode ->
                        FilterChip(
                            selected = settings.themeMode == mode,
                            onClick = { onThemeModeChanged(mode) },
                            label = {
                                Text(
                                    text = mode.name.lowercase().replaceFirstChar { it.uppercase() },
                                    fontSize = 13.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BronzeAccent,
                                selectedLabelColor = MidnightBackground,
                                containerColor = SlateSurfaceVariant,
                                labelColor = TextWhite
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = settings.themeMode == mode,
                                borderColor = BronzeGrid,
                                selectedBorderColor = BronzeAccent
                            )
                        )
                    }
                }
            }

            // Board Theme
            SettingsSectionCard(title = "BOARD FINISH") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BoardThemeStyle.entries.forEach { style ->
                        FilterChip(
                            selected = settings.boardTheme == style,
                            onClick = { onBoardThemeChanged(style) },
                            label = {
                                Text(
                                    text = when (style) {
                                        BoardThemeStyle.CLASSIC -> "Classic Slate"
                                        BoardThemeStyle.MIDNIGHT -> "Midnight Obsidian"
                                    },
                                    fontSize = 13.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SapphirePrimary,
                                selectedLabelColor = Color.White,
                                containerColor = SlateSurfaceVariant,
                                labelColor = TextWhite
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = settings.boardTheme == style,
                                borderColor = BronzeGrid,
                                selectedBorderColor = SapphirePrimary
                            )
                        )
                    }
                }
            }

            // Gameplay Feedback & Indicators
            SettingsSectionCard(title = "GAMEPLAY & AUDIO") {
                SettingsSwitchRow(
                    title = "Sound Effects",
                    subtitle = "Harmonic synthesized tones for nodes & bridges",
                    checked = settings.soundEnabled,
                    onCheckedChange = onSoundChanged
                )

                Spacer(modifier = Modifier.height(10.dp))

                SettingsSwitchRow(
                    title = "Haptic Feedback",
                    subtitle = "Tactile response on placements and connections",
                    checked = settings.hapticsEnabled,
                    onCheckedChange = onHapticsChanged
                )

                Spacer(modifier = Modifier.height(10.dp))

                SettingsSwitchRow(
                    title = "Show Last Move",
                    subtitle = "Subtle glowing ring on the most recently placed node",
                    checked = settings.showLastMove,
                    onCheckedChange = onShowLastMoveChanged
                )

                Spacer(modifier = Modifier.height(10.dp))

                SettingsSwitchRow(
                    title = "Highlight Target Sides",
                    subtitle = "Show player color rails on target board edges",
                    checked = settings.highlightTargetSides,
                    onCheckedChange = onHighlightTargetSidesChanged
                )
            }

            // Data & Statistics Management
            SettingsSectionCard(title = "STATISTICS & DATA") {
                Text(
                    text = "Clear all accumulated match statistics, win counts, and shortest path records.",
                    color = TextMuted,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = { showResetDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CopperPrimary),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CopperPrimary.copy(alpha = 0.6f))
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = null,
                        tint = CopperPrimary
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Text("Reset Match Statistics", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SettingsSectionCard(
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
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(14.dp))
            content()
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = TextWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                color = TextMuted,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = SapphirePrimary,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = SlateSurfaceVariant
            )
        )
    }
}
