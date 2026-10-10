package com.japaneixxx.antsnravens.api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Small dependency-free graph contract used by server and compatibility modules.
 */
public final class RoutingGraph {
    private final Map<UUID, Set<UUID>> links = new HashMap<>();

    public void addNode(NetworkNode node) {
        links.computeIfAbsent(node.id(), ignored -> new HashSet<>());
    }

    public void connect(NetworkNode first, NetworkNode second) {
        addNode(first);
        addNode(second);
        links.get(first.id()).add(second.id());
        links.get(second.id()).add(first.id());
    }

    public boolean isConnected(UUID first, UUID second) {
        if (first.equals(second)) {
            return links.containsKey(first);
        }
        Set<UUID> visited = new HashSet<>();
        ArrayDeque<UUID> pending = new ArrayDeque<>();
        pending.add(first);
        while (!pending.isEmpty()) {
            UUID current = pending.removeFirst();
            if (!visited.add(current)) {
                continue;
            }
            for (UUID neighbor : links.getOrDefault(current, Set.of())) {
                if (neighbor.equals(second)) {
                    return true;
                }
                pending.addLast(neighbor);
            }
        }
        return false;
    }

    public void disconnect(NetworkNode first, NetworkNode second) {
        links.getOrDefault(first.id(), Set.of()).remove(second.id());
        links.getOrDefault(second.id(), Set.of()).remove(first.id());
    }

    public int nodeCount() {
        return links.size();
    }
}
