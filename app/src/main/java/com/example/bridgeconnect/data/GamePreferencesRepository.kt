package com.example.bridgeconnect.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "bridge_connect_prefs")

enum class AppThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

enum class BoardThemeStyle {
    CLASSIC,
    MIDNIGHT
}

data class UserSettings(
    val themeMode: AppThemeMode = AppThemeMode.DARK,
    val boardTheme: BoardThemeStyle = BoardThemeStyle.CLASSIC,
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val showLastMove: Boolean = true,
    val highlightTargetSides: Boolean = true
)

data class GameStatistics(
    val gamesPlayed: Int = 0,
    val player1Wins: Int = 0,
    val player2Wins: Int = 0,
    val draws: Int = 0,
    val shortestWinningPath: Int = 0
)

class GamePreferencesRepository(private val context: Context) {

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val BOARD_THEME = stringPreferencesKey("board_theme")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
        val SHOW_LAST_MOVE = booleanPreferencesKey("show_last_move")
        val HIGHLIGHT_TARGET_SIDES = booleanPreferencesKey("highlight_target_sides")

        val GAMES_PLAYED = intPreferencesKey("games_played")
        val PLAYER_1_WINS = intPreferencesKey("player_1_wins")
        val PLAYER_2_WINS = intPreferencesKey("player_2_wins")
        val DRAWS = intPreferencesKey("draws")
        val SHORTEST_WINNING_PATH = intPreferencesKey("shortest_winning_path")
    }

    val settingsFlow: Flow<UserSettings> = context.dataStore.data.map { prefs ->
        UserSettings(
            themeMode = prefs[Keys.THEME_MODE]?.let { runCatching { AppThemeMode.valueOf(it) }.getOrNull() }
                ?: AppThemeMode.DARK,
            boardTheme = prefs[Keys.BOARD_THEME]?.let { runCatching { BoardThemeStyle.valueOf(it) }.getOrNull() }
                ?: BoardThemeStyle.CLASSIC,
            soundEnabled = prefs[Keys.SOUND_ENABLED] ?: true,
            hapticsEnabled = prefs[Keys.HAPTICS_ENABLED] ?: true,
            showLastMove = prefs[Keys.SHOW_LAST_MOVE] ?: true,
            highlightTargetSides = prefs[Keys.HIGHLIGHT_TARGET_SIDES] ?: true
        )
    }

    val statisticsFlow: Flow<GameStatistics> = context.dataStore.data.map { prefs ->
        GameStatistics(
            gamesPlayed = prefs[Keys.GAMES_PLAYED] ?: 0,
            player1Wins = prefs[Keys.PLAYER_1_WINS] ?: 0,
            player2Wins = prefs[Keys.PLAYER_2_WINS] ?: 0,
            draws = prefs[Keys.DRAWS] ?: 0,
            shortestWinningPath = prefs[Keys.SHORTEST_WINNING_PATH] ?: 0
        )
    }

    suspend fun setThemeMode(mode: AppThemeMode) {
        context.dataStore.edit { prefs ->
            prefs[Keys.THEME_MODE] = mode.name
        }
    }

    suspend fun setBoardTheme(theme: BoardThemeStyle) {
        context.dataStore.edit { prefs ->
            prefs[Keys.BOARD_THEME] = theme.name
        }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SOUND_ENABLED] = enabled
        }
    }

    suspend fun setHapticsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.HAPTICS_ENABLED] = enabled
        }
    }

    suspend fun setShowLastMove(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SHOW_LAST_MOVE] = enabled
        }
    }

    suspend fun setHighlightTargetSides(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.HIGHLIGHT_TARGET_SIDES] = enabled
        }
    }

    suspend fun recordGameResult(winner: com.example.bridgeconnect.model.Player?, winningPathLength: Int?) {
        context.dataStore.edit { prefs ->
            val games = (prefs[Keys.GAMES_PLAYED] ?: 0) + 1
            prefs[Keys.GAMES_PLAYED] = games

            if (winner == null) {
                prefs[Keys.DRAWS] = (prefs[Keys.DRAWS] ?: 0) + 1
            } else {
                when (winner) {
                    com.example.bridgeconnect.model.Player.PLAYER_ONE -> {
                        prefs[Keys.PLAYER_1_WINS] = (prefs[Keys.PLAYER_1_WINS] ?: 0) + 1
                    }
                    com.example.bridgeconnect.model.Player.PLAYER_TWO -> {
                        prefs[Keys.PLAYER_2_WINS] = (prefs[Keys.PLAYER_2_WINS] ?: 0) + 1
                    }
                }

                if (winningPathLength != null && winningPathLength > 0) {
                    val currentShortest = prefs[Keys.SHORTEST_WINNING_PATH] ?: 0
                    if (currentShortest == 0 || winningPathLength < currentShortest) {
                        prefs[Keys.SHORTEST_WINNING_PATH] = winningPathLength
                    }
                }
            }
        }
    }

    suspend fun resetStatistics() {
        context.dataStore.edit { prefs ->
            prefs[Keys.GAMES_PLAYED] = 0
            prefs[Keys.PLAYER_1_WINS] = 0
            prefs[Keys.PLAYER_2_WINS] = 0
            prefs[Keys.DRAWS] = 0
            prefs[Keys.SHORTEST_WINNING_PATH] = 0
        }
    }
}
