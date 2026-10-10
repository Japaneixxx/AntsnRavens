package com.japaneixxx.antsnravens.api;

import java.util.UUID;

/**
 * Public identity for a node in an Ants 'n Ravens colony network.
 */
public record NetworkNode(UUID id, NodeType type) {
    public NetworkNode {
        if (id == null || type == null) {
            throw new IllegalArgumentException("A network node requires an id and a type");
        }
    }

    public enum NodeType {
        QUEEN_CHAMBER,
        SUB_COLONY
    }
}
