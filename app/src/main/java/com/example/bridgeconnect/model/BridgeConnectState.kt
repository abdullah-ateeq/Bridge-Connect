package com.example.bridgeconnect.model

data class BridgeConnectState(
    val owners: Map<BridgeNode, Player> = emptyMap(),
    val bridges: Set<BridgeEdge> = emptySet(),
    val currentPlayer: Player = Player.PLAYER_ONE,
    val lastMove: BridgeNode? = null,
    val newlyFormedBridges: Set<BridgeEdge> = emptySet(),
    val winningPath: List<BridgeNode> = emptyList(),
    val winner: Player? = null,
    val isDraw: Boolean = false,
    val moveCount: Int = 0,
    val isAnimating: Boolean = false,
    val matchStartTimeMillis: Long = System.currentTimeMillis(),
    val matchDurationSeconds: Long = 0L,
    val canUndo: Boolean = false
) {
    val isGameOver: Boolean get() = winner != null || isDraw

    val player1NodeCount: Int get() = owners.values.count { it == Player.PLAYER_ONE }
    val player2NodeCount: Int get() = owners.values.count { it == Player.PLAYER_TWO }

    val player1BridgeCount: Int get() = bridges.count { edge ->
        owners[edge.a] == Player.PLAYER_ONE && owners[edge.b] == Player.PLAYER_ONE
    }
    val player2BridgeCount: Int get() = bridges.count { edge ->
        owners[edge.a] == Player.PLAYER_TWO && owners[edge.b] == Player.PLAYER_TWO
    }

    val totalBridgeCount: Int get() = bridges.size

    val winningEdges: Set<BridgeEdge> by lazy {
        if (winningPath.size < 2) emptySet()
        else {
            val edges = mutableSetOf<BridgeEdge>()
            for (i in 0 until winningPath.size - 1) {
                edges.add(BridgeEdge.of(winningPath[i], winningPath[i + 1]))
            }
            edges
        }
    }

    fun toSnapshot(): GameSnapshot = GameSnapshot(
        owners = owners,
        bridges = bridges,
        currentPlayer = currentPlayer,
        lastMove = lastMove,
        winningPath = winningPath,
        winner = winner,
        isDraw = isDraw,
        moveCount = moveCount
    )

    fun restore(snapshot: GameSnapshot, canUndoRemaining: Boolean): BridgeConnectState = copy(
        owners = snapshot.owners,
        bridges = snapshot.bridges,
        currentPlayer = snapshot.currentPlayer,
        lastMove = snapshot.lastMove,
        newlyFormedBridges = emptySet(),
        winningPath = snapshot.winningPath,
        winner = snapshot.winner,
        isDraw = snapshot.isDraw,
        moveCount = snapshot.moveCount,
        isAnimating = false,
        canUndo = canUndoRemaining
    )
}
