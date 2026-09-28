package com.example.bridgeconnect.model

data class BridgeEdge(
    val a: BridgeNode,
    val b: BridgeNode
) {
    init {
        require(a <= b) { "BridgeEdge endpoints must be ordered (a <= b), use BridgeEdge.of(n1, n2)" }
    }

    fun contains(node: BridgeNode): Boolean = a == node || b == node

    fun other(node: BridgeNode): BridgeNode? {
        return when (node) {
            a -> b
            b -> a
            else -> null
        }
    }

    companion object {
        fun of(n1: BridgeNode, n2: BridgeNode): BridgeEdge {
            return if (n1 <= n2) BridgeEdge(n1, n2) else BridgeEdge(n2, n1)
        }
    }
}
