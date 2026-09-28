package com.example.bridgeconnect.model

enum class Player(val displayName: String, val objective: String) {
    PLAYER_ONE("Player 1", "Connect Top ↔ Bottom"),
    PLAYER_TWO("Player 2", "Connect Left ↔ Right");

    val other: Player
        get() = if (this == PLAYER_ONE) PLAYER_TWO else PLAYER_ONE
}
