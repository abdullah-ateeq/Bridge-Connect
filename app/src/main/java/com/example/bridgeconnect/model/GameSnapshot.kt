package com.example.bridgeconnect.model

data class GameSnapshot(
    val owners: Map<BridgeNode, Player>,
    val bridges: Set<BridgeEdge>,
    val currentPlayer: Player,
    val lastMove: BridgeNode?,
    val winningPath: List<BridgeNode>,
    val winner: Player?,
    val isDraw: Boolean,
    val moveCount: Int
)
