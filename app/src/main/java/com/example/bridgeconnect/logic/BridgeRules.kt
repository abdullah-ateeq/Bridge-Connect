package com.example.bridgeconnect.logic

import com.example.bridgeconnect.model.BridgeEdge
import com.example.bridgeconnect.model.BridgeNode
import com.example.bridgeconnect.model.Player

object BridgeRules {
    const val BOARD_SIZE = 7
    const val TOTAL_NODES = 49

    /**
     * Fixed knight-style relative offsets:
     * (±2, ±1) and (±1, ±2)
     */
    val bridgeOffsets = listOf(
        -2 to -1,
        -2 to 1,
        -1 to -2,
        -1 to 2,
        1 to -2,
        1 to 2,
        2 to -1,
        2 to 1
    )

    fun isInsideBoard(row: Int, col: Int): Boolean {
        return row in 0 until BOARD_SIZE && col in 0 until BOARD_SIZE
    }

    fun isInsideBoard(node: BridgeNode): Boolean {
        return isInsideBoard(node.row, node.col)
    }

    /**
     * Finds all nodes owned by the same player that can form a bridge with the given node.
     */
    fun getBridgeNeighbors(
        node: BridgeNode,
        owners: Map<BridgeNode, Player>,
        player: Player
    ): List<BridgeNode> {
        val neighbors = mutableListOf<BridgeNode>()
        for ((dr, dc) in bridgeOffsets) {
            val nr = node.row + dr
            val nc = node.col + dc
            if (isInsideBoard(nr, nc)) {
                val candidate = BridgeNode(nr, nc)
                if (owners[candidate] == player) {
                    neighbors.add(candidate)
                }
            }
        }
        return neighbors
    }

    /**
     * Generates all new bridge connections formed by placing a node.
     */
    fun findNewBridges(
        placedNode: BridgeNode,
        owners: Map<BridgeNode, Player>,
        player: Player
    ): Set<BridgeEdge> {
        val newEdges = mutableSetOf<BridgeEdge>()
        val neighbors = getBridgeNeighbors(placedNode, owners, player)
        for (neighbor in neighbors) {
            newEdges.add(BridgeEdge.of(placedNode, neighbor))
        }
        return newEdges
    }

    /**
     * Searches for a winning path using Breadth-First Search (BFS) for the given player.
     * Player 1 wins by connecting Top (row 0) to Bottom (row 6).
     * Player 2 wins by connecting Left (col 0) to Right (col 6).
     * Returns the reconstructed path of nodes in order from start edge to target edge, or null.
     */
    fun findWinningPath(
        owners: Map<BridgeNode, Player>,
        bridges: Set<BridgeEdge>,
        player: Player
    ): List<BridgeNode>? {
        // Build adjacency graph for the specified player
        val adj = mutableMapOf<BridgeNode, MutableList<BridgeNode>>()
        for (edge in bridges) {
            if (owners[edge.a] == player && owners[edge.b] == player) {
                adj.getOrPut(edge.a) { mutableListOf() }.add(edge.b)
                adj.getOrPut(edge.b) { mutableListOf() }.add(edge.a)
            }
        }

        // Identify starting nodes touching the initial edge
        val startNodes = owners.filter { (node, p) ->
            p == player && when (player) {
                Player.PLAYER_ONE -> node.row == 0
                Player.PLAYER_TWO -> node.col == 0
            }
        }.keys

        if (startNodes.isEmpty()) return null

        val isTarget: (BridgeNode) -> Boolean = { node ->
            when (player) {
                Player.PLAYER_ONE -> node.row == BOARD_SIZE - 1
                Player.PLAYER_TWO -> node.col == BOARD_SIZE - 1
            }
        }

        val queue = ArrayDeque<BridgeNode>()
        val visited = mutableSetOf<BridgeNode>()
        val parent = mutableMapOf<BridgeNode, BridgeNode?>()

        for (start in startNodes) {
            queue.add(start)
            visited.add(start)
            parent[start] = null
        }

        var winningTarget: BridgeNode? = null

        while (queue.isNotEmpty()) {
            val curr = queue.removeFirst()
            if (isTarget(curr)) {
                winningTarget = curr
                break
            }

            val neighbors = adj[curr] ?: emptyList()
            for (neighbor in neighbors) {
                if (visited.add(neighbor)) {
                    parent[neighbor] = curr
                    queue.add(neighbor)
                }
            }
        }

        if (winningTarget == null) return null

        // Reconstruct path from target back to start
        val path = mutableListOf<BridgeNode>()
        var step: BridgeNode? = winningTarget
        while (step != null) {
            path.add(step)
            step = parent[step]
        }
        return path.reversed()
    }
}
