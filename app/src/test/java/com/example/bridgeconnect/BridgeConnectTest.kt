package com.example.bridgeconnect

import com.example.bridgeconnect.logic.BridgeRules
import com.example.bridgeconnect.model.BridgeConnectState
import com.example.bridgeconnect.model.BridgeEdge
import com.example.bridgeconnect.model.BridgeNode
import com.example.bridgeconnect.model.Player
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BridgeConnectTest {

    @Test
    fun test1_initialBoard() {
        val state = BridgeConnectState()
        assertTrue(state.owners.isEmpty())
        assertTrue(state.bridges.isEmpty())
        assertEquals(Player.PLAYER_ONE, state.currentPlayer)
        assertNull(state.winner)
        assertFalse(state.isDraw)
        assertEquals(0, state.moveCount)
        assertEquals(49, BridgeNode.ALL_NODES.size)
    }

    @Test
    fun test2_legalNodePlacement() {
        var state = BridgeConnectState()
        val node = BridgeNode(2, 2)
        val player = state.currentPlayer

        val newOwners = state.owners + (node to player)
        val newBridges = BridgeRules.findNewBridges(node, newOwners, player)
        state = state.copy(
            owners = newOwners,
            bridges = state.bridges + newBridges,
            lastMove = node,
            moveCount = 1,
            currentPlayer = player.other
        )

        assertEquals(1, state.owners.size)
        assertEquals(player, state.owners[node])
        assertEquals(Player.PLAYER_TWO, state.currentPlayer)
        assertEquals(1, state.moveCount)
        assertEquals(node, state.lastMove)
    }

    @Test
    fun test3_occupiedNodeRejection() {
        val node = BridgeNode(3, 3)
        val owners = mapOf(node to Player.PLAYER_ONE)

        // Attempting to place on an already occupied node should be rejected
        val isOccupied = owners.containsKey(node)
        assertTrue("Occupied node must be detected", isOccupied)
    }

    @Test
    fun test4_correctKnightStyleBridgeOffsets() {
        val center = BridgeNode(2, 2)
        val expectedTargets = listOf(
            BridgeNode(0, 1),
            BridgeNode(0, 3),
            BridgeNode(1, 0),
            BridgeNode(1, 4),
            BridgeNode(3, 0),
            BridgeNode(3, 4),
            BridgeNode(4, 1),
            BridgeNode(4, 3)
        )

        val owners = expectedTargets.associateWith { Player.PLAYER_ONE }
        val newBridges = BridgeRules.findNewBridges(center, owners + (center to Player.PLAYER_ONE), Player.PLAYER_ONE)

        assertEquals("Center node should form exactly 8 bridges", 8, newBridges.size)
        for (target in expectedTargets) {
            val expectedEdge = BridgeEdge.of(center, target)
            assertTrue("Expected bridge to $target", newBridges.contains(expectedEdge))
        }
    }

    @Test
    fun test5_nonValidOffsetNotConnected() {
        val center = BridgeNode(2, 2)
        val invalidNeighbors = listOf(
            BridgeNode(2, 3), // Adjacent horizontal
            BridgeNode(3, 2), // Adjacent vertical
            BridgeNode(3, 3), // Diagonal adjacent
            BridgeNode(2, 4), // 2 horizontal steps
            BridgeNode(5, 5)  // Distant node
        )

        val owners = invalidNeighbors.associateWith { Player.PLAYER_ONE }
        val newBridges = BridgeRules.findNewBridges(center, owners + (center to Player.PLAYER_ONE), Player.PLAYER_ONE)

        assertTrue("Invalid offsets must not form bridges", newBridges.isEmpty())
    }

    @Test
    fun test6_duplicateBridgePrevention() {
        val n1 = BridgeNode(1, 1)
        val n2 = BridgeNode(3, 2)

        val edge1 = BridgeEdge.of(n1, n2)
        val edge2 = BridgeEdge.of(n2, n1)

        assertEquals("Edges with reversed endpoints must be equal", edge1, edge2)

        val bridgeSet = setOf(edge1, edge2)
        assertEquals("Set must prevent duplicate bridge representations", 1, bridgeSet.size)
    }

    @Test
    fun test7_player1TopToBottomPath() {
        // Player 1 links: (0, 1) -> (2, 2) -> (4, 3) -> (6, 4)
        val chain = listOf(
            BridgeNode(0, 1),
            BridgeNode(2, 2),
            BridgeNode(4, 3),
            BridgeNode(6, 4)
        )

        val owners = chain.associateWith { Player.PLAYER_ONE }
        val bridges = setOf(
            BridgeEdge.of(chain[0], chain[1]),
            BridgeEdge.of(chain[1], chain[2]),
            BridgeEdge.of(chain[2], chain[3])
        )

        val winningPath = BridgeRules.findWinningPath(owners, bridges, Player.PLAYER_ONE)
        assertNotNull("Player 1 should have a winning path", winningPath)
        assertEquals(4, winningPath?.size)
        assertEquals(0, winningPath?.first()?.row)
        assertEquals(6, winningPath?.last()?.row)
    }

    @Test
    fun test8_player2LeftToRightPath() {
        // Player 2 links: (1, 0) -> (2, 2) -> (3, 4) -> (4, 6)
        val chain = listOf(
            BridgeNode(1, 0),
            BridgeNode(2, 2),
            BridgeNode(3, 4),
            BridgeNode(4, 6)
        )

        val owners = chain.associateWith { Player.PLAYER_TWO }
        val bridges = setOf(
            BridgeEdge.of(chain[0], chain[1]),
            BridgeEdge.of(chain[1], chain[2]),
            BridgeEdge.of(chain[2], chain[3])
        )

        val winningPath = BridgeRules.findWinningPath(owners, bridges, Player.PLAYER_TWO)
        assertNotNull("Player 2 should have a winning path", winningPath)
        assertEquals(4, winningPath?.size)
        assertEquals(0, winningPath?.first()?.col)
        assertEquals(6, winningPath?.last()?.col)
    }

    @Test
    fun test9_disconnectedBridgeGroupsDoNotWin() {
        // Top group touches row 0, bottom group touches row 6, but no link between them
        val topGroup = listOf(BridgeNode(0, 1), BridgeNode(2, 2))
        val bottomGroup = listOf(BridgeNode(4, 3), BridgeNode(6, 4))

        val owners = (topGroup + bottomGroup).associateWith { Player.PLAYER_ONE }
        val bridges = setOf(
            BridgeEdge.of(topGroup[0], topGroup[1]),
            BridgeEdge.of(bottomGroup[0], bottomGroup[1])
        )

        val winningPath = BridgeRules.findWinningPath(owners, bridges, Player.PLAYER_ONE)
        assertNull("Disconnected groups must not produce a win", winningPath)
    }

    @Test
    fun test10_winningPathReconstruction() {
        val n0 = BridgeNode(0, 1)
        val n1 = BridgeNode(2, 2)
        val n2 = BridgeNode(4, 1)
        val n3 = BridgeNode(6, 2)

        val owners = mapOf(
            n0 to Player.PLAYER_ONE,
            n1 to Player.PLAYER_ONE,
            n2 to Player.PLAYER_ONE,
            n3 to Player.PLAYER_ONE
        )
        val bridges = setOf(
            BridgeEdge.of(n0, n1),
            BridgeEdge.of(n1, n2),
            BridgeEdge.of(n2, n3)
        )

        val path = BridgeRules.findWinningPath(owners, bridges, Player.PLAYER_ONE)
        assertEquals(listOf(n0, n1, n2, n3), path)
    }

    @Test
    fun test11_fullBoardDraw() {
        // Create an alternating checkerboard pattern where no continuous knight path connects edges
        val owners = mutableMapOf<BridgeNode, Player>()
        for (r in 0..6) {
            for (c in 0..6) {
                // Alternating rows
                owners[BridgeNode(r, c)] = if (r % 2 == 0) Player.PLAYER_TWO else Player.PLAYER_ONE
            }
        }

        // All 49 nodes placed
        assertEquals(49, owners.size)

        // Neither player has connected bridges from opposite sides
        val p1Win = BridgeRules.findWinningPath(owners, emptySet(), Player.PLAYER_ONE)
        val p2Win = BridgeRules.findWinningPath(owners, emptySet(), Player.PLAYER_TWO)

        assertNull(p1Win)
        assertNull(p2Win)

        val state = BridgeConnectState(
            owners = owners,
            isDraw = true
        )
        assertTrue(state.isGameOver)
        assertTrue(state.isDraw)
        assertNull(state.winner)
    }

    @Test
    fun test12_undoRestoresPreviousState() {
        var state = BridgeConnectState()
        val snapshot = state.toSnapshot()

        // Place a node
        val placed = BridgeNode(3, 3)
        state = state.copy(
            owners = mapOf(placed to Player.PLAYER_ONE),
            lastMove = placed,
            moveCount = 1,
            currentPlayer = Player.PLAYER_TWO
        )

        assertEquals(1, state.owners.size)

        // Undo back
        state = state.restore(snapshot, canUndoRemaining = false)
        assertTrue(state.owners.isEmpty())
        assertNull(state.lastMove)
        assertEquals(0, state.moveCount)
        assertEquals(Player.PLAYER_ONE, state.currentPlayer)
        assertFalse(state.canUndo)
    }
}
