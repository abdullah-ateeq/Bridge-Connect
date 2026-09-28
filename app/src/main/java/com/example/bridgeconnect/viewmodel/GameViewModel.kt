package com.example.bridgeconnect.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bridgeconnect.audio.SoundManager
import com.example.bridgeconnect.data.GamePreferencesRepository
import com.example.bridgeconnect.data.UserSettings
import com.example.bridgeconnect.logic.BridgeRules
import com.example.bridgeconnect.model.BridgeConnectState
import com.example.bridgeconnect.model.BridgeNode
import com.example.bridgeconnect.model.GameSnapshot
import com.example.bridgeconnect.model.Player
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GamePreferencesRepository(application.applicationContext)
    val soundManager = SoundManager()

    val settings: StateFlow<UserSettings> = repository.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = UserSettings()
    )

    private val _state = MutableStateFlow(BridgeConnectState())
    val state: StateFlow<BridgeConnectState> = _state.asStateFlow()

    private val undoStack = ArrayDeque<GameSnapshot>()
    private var timerJob: Job? = null

    init {
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                if (!_state.value.isGameOver) {
                    _state.value = _state.value.copy(
                        matchDurationSeconds = _state.value.matchDurationSeconds + 1
                    )
                }
            }
        }
    }

    fun placeNode(node: BridgeNode, onWinAnimationTrigger: () -> Unit = {}) {
        val current = _state.value
        if (current.isGameOver || current.isAnimating) return
        if (!BridgeRules.isInsideBoard(node)) return
        if (current.owners.containsKey(node)) return

        // 1. Save Undo snapshot
        undoStack.addLast(current.toSnapshot())

        // 2. Lock input
        val player = current.currentPlayer
        val newOwners = current.owners + (node to player)
        val newBridges = BridgeRules.findNewBridges(node, newOwners, player)
        val allBridges = current.bridges + newBridges

        val soundOn = settings.value.soundEnabled
        if (newBridges.isNotEmpty()) {
            soundManager.playBridgeFormed(soundOn)
        } else {
            soundManager.playNodePlaced(soundOn)
        }

        // Animate placement and bridge drawing
        _state.value = current.copy(
            owners = newOwners,
            bridges = allBridges,
            newlyFormedBridges = newBridges,
            lastMove = node,
            moveCount = current.moveCount + 1,
            isAnimating = true,
            canUndo = true
        )

        viewModelScope.launch {
            // Keep animation short (140ms)
            delay(140)

            // Check winning path
            val winningPath = BridgeRules.findWinningPath(newOwners, allBridges, player)
            if (winningPath != null) {
                _state.value = _state.value.copy(
                    winner = player,
                    winningPath = winningPath,
                    isAnimating = false,
                    newlyFormedBridges = emptySet()
                )
                soundManager.playWin(settings.value.soundEnabled)
                onWinAnimationTrigger()
                repository.recordGameResult(player, winningPath.size)
            } else if (newOwners.size >= BridgeRules.TOTAL_NODES) {
                _state.value = _state.value.copy(
                    isDraw = true,
                    isAnimating = false,
                    newlyFormedBridges = emptySet()
                )
                soundManager.playDraw(settings.value.soundEnabled)
                repository.recordGameResult(null, null)
            } else {
                // Switch turn
                _state.value = _state.value.copy(
                    currentPlayer = player.other,
                    isAnimating = false,
                    newlyFormedBridges = emptySet()
                )
                soundManager.playTurnSwitch(settings.value.soundEnabled)
            }
        }
    }

    fun undo() {
        val current = _state.value
        if (current.isAnimating || undoStack.isEmpty()) return

        val snapshot = undoStack.removeLast()
        _state.value = current.restore(snapshot, canUndoRemaining = undoStack.isNotEmpty())
    }

    fun restart() {
        undoStack.clear()
        _state.value = BridgeConnectState(
            matchStartTimeMillis = System.currentTimeMillis()
        )
        startTimer()
    }

    fun playAgain() {
        restart()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
