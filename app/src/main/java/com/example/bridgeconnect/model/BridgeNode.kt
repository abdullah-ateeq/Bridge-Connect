package com.example.bridgeconnect.model

data class BridgeNode(
    val row: Int,
    val col: Int
) : Comparable<BridgeNode> {
    init {
        require(row in 0..6 && col in 0..6) { "Node coordinates out of bounds: ($row, $col)" }
    }

    fun touchesTop(): Boolean = row == 0
    fun touchesBottom(): Boolean = row == 6
    fun touchesLeft(): Boolean = col == 0
    fun touchesRight(): Boolean = col == 6

    fun touchesPlayerTarget(player: Player): Boolean {
        return when (player) {
            Player.PLAYER_ONE -> touchesTop() || touchesBottom()
            Player.PLAYER_TWO -> touchesLeft() || touchesRight()
        }
    }

    override fun compareTo(other: BridgeNode): Int {
        val r = row.compareTo(other.row)
        return if (r != 0) r else col.compareTo(other.col)
    }

    companion object {
        const val BOARD_SIZE = 7
        const val TOTAL_NODES = 49

        val ALL_NODES: List<BridgeNode> by lazy {
            (0 until BOARD_SIZE).flatMap { r ->
                (0 until BOARD_SIZE).map { c ->
                    BridgeNode(r, c)
                }
            }
        }
    }
}
