package com.japaneixxx.antsnravens.network;

import com.japaneixxx.antsnravens.ModConfigSpecProvider;
import com.japaneixxx.antsnravens.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public final class ColonyNetworkLocator {
    private ColonyNetworkLocator() {
    }

    public static NetworkSnapshot scan(Level level, BlockPos origin) {
        int radius = ModConfigSpecProvider.NETWORK_SCAN_RADIUS.get();
        int queens = 0;
        int nodes = 0;
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    cursor.set(origin.getX() + x, origin.getY() + y, origin.getZ() + z);
                    if (!level.isLoaded(cursor)) {
                        continue;
                    }
                    var block = level.getBlockState(cursor).getBlock();
                    if (block == ModBlocks.QUEEN_CHAMBER.get()) {
                        queens++;
                    } else if (block == ModBlocks.SUB_COLONY_NODE.get()) {
                        nodes++;
                    }
                }
            }
        }
        return new NetworkSnapshot(queens, nodes, radius);
    }

    public record NetworkSnapshot(int queenChambers, int subColonyNodes, int radius) {
        public boolean isOperational() {
            return queenChambers == 1;
        }
    }
}
