package com.japaneixxx.antsnravens.world.inventory;

import com.japaneixxx.antsnravens.block.entity.QueenChamberBlockEntity;
import com.japaneixxx.antsnravens.block.entity.SubColonyNodeBlockEntity;
import com.japaneixxx.antsnravens.entity.CompanionCrowEntity;
import com.japaneixxx.antsnravens.init.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class RavenTerminalMenu extends AbstractContainerMenu {
    private static final int CENTRAL_SLOT_COUNT = 27;
    private static final int SUB_COLONY_SLOT_LIMIT = 36;
    private static final int SUB_COLONY_BACKING_LIMIT = 441;
    private static final int VISIBLE_SUB_COLONY_ROWS = 4;
    private final BlockPos queenPos;
    private final Container networkInventory;
    private final Container subColonyInventory;
    private final ContainerData networkData;
    private final CompanionCrowEntity companionCrow;
    private int scrollRow;

    public RavenTerminalMenu(int containerId, Inventory inventory, QueenChamberBlockEntity queen) {
        this(containerId, inventory, queen.getBlockPos(), queen.getCentralInventory(),
                createSubColonyInventory(queen), new SimpleContainerData(3), null);
        updateData(queen);
    }

    public RavenTerminalMenu(int containerId, Inventory inventory, BlockPos queenPos, CompanionCrowEntity crow) {
        this(containerId, inventory, queenPos, new SimpleContainer(CENTRAL_SLOT_COUNT),
                new ScrollingContainer(new SimpleContainer(SUB_COLONY_BACKING_LIMIT),
                        SUB_COLONY_SLOT_LIMIT), new SimpleContainerData(3), crow);
        if (crow.level().getBlockEntity(queenPos) instanceof QueenChamberBlockEntity queen) {
            updateData(queen);
        }
    }

    public RavenTerminalMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf data) {
        this(containerId, inventory, data == null ? BlockPos.ZERO : data.readBlockPos(),
                new SimpleContainer(CENTRAL_SLOT_COUNT),
                new ScrollingContainer(new SimpleContainer(SUB_COLONY_BACKING_LIMIT),
                        SUB_COLONY_SLOT_LIMIT),
                new SimpleContainerData(3), null);
    }

    private RavenTerminalMenu(int containerId, Inventory inventory, BlockPos queenPos,
            Container networkInventory, Container subColonyInventory, ContainerData networkData,
            CompanionCrowEntity companionCrow) {
        super(ModMenuTypes.RAVEN_TERMINAL.get(), containerId);
        this.queenPos = queenPos.immutable();
        this.networkInventory = networkInventory;
        this.subColonyInventory = subColonyInventory;
        this.networkData = networkData;
        this.companionCrow = companionCrow;
        for (int slot = 0; slot < CENTRAL_SLOT_COUNT; slot++) {
            addSlot(new Slot(networkInventory, slot, 8 + (slot % 9) * 18, 30 + (slot / 9) * 18));
        }
        for (int slot = 0; slot < SUB_COLONY_SLOT_LIMIT; slot++) {
            addSlot(new DynamicSlot(subColonyInventory, slot, 8 + (slot % 9) * 18,
                    82 + (slot / 9) * 18, this));
        }
        addDataSlots(networkData);
        addPlayerInventory(inventory);
    }

    private static Container createSubColonyInventory(QueenChamberBlockEntity queen) {
        for (BlockPos nodePos : queen.getConnectedSubColonies()) {
            if (queen.getLevel() != null
                    && queen.getLevel().getBlockEntity(nodePos) instanceof SubColonyNodeBlockEntity node) {
                node.scanAdjacentInventories();
                return new ScrollingContainer(
                        new PaddedContainer(node.getInventoryView(), SUB_COLONY_BACKING_LIMIT),
                        SUB_COLONY_SLOT_LIMIT);
            }
        }
        return new SimpleContainer(SUB_COLONY_SLOT_LIMIT);
    }

    private void updateData(QueenChamberBlockEntity queen) {
        networkData.set(0, queen.getConnectedSubColonies().size());
        int connectedChests = 0;
        int totalSlots = 0;
        for (BlockPos nodePos : queen.getConnectedSubColonies()) {
            if (queen.getLevel() != null
                    && queen.getLevel().getBlockEntity(nodePos) instanceof SubColonyNodeBlockEntity node) {
                connectedChests += node.getConnectedChestCount();
                totalSlots = node.getTotalSlotCount();
                break;
            }
        }
        networkData.set(1, connectedChests);
        networkData.set(2, totalSlots);
    }

    private void addPlayerInventory(Inventory inventory) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 166 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(inventory, column, 8 + column * 18, 220));
        }
    }

    public BlockPos getQueenPos() {
        return queenPos;
    }

    public int getConnectedSubColonyCount() {
        return networkData.get(0);
    }

    public int getConnectedChestCount() {
        return networkData.get(1);
    }

    public int getSubColonySlotCount() {
        return networkData.get(2);
    }

    public int getScrollRow() {
        return scrollRow;
    }

    public int getMaxScrollRow() {
        int rows = Math.max(1, (getSubColonySlotCount() + 8) / 9);
        return Math.max(0, rows - VISIBLE_SUB_COLONY_ROWS);
    }

    public void setScrollRow(int row) {
        scrollRow = Math.max(0, Math.min(row, getMaxScrollRow()));
        if (subColonyInventory instanceof ScrollingContainer scrolling) {
            scrolling.setOffset(scrollRow * 9);
        }
    }

    public void setScrollFraction(double fraction) {
        setScrollRow((int) Math.round(Math.max(0.0, Math.min(1.0, fraction)) * getMaxScrollRow()));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = getSlot(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack original = slot.getItem().copy();
        ItemStack moving = slot.getItem();
        if (index < CENTRAL_SLOT_COUNT) {
            if (!moveItemStackTo(moving, CENTRAL_SLOT_COUNT, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (index < CENTRAL_SLOT_COUNT + SUB_COLONY_SLOT_LIMIT) {
            if (!moveItemStackTo(moving, 0, CENTRAL_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(moving, CENTRAL_SLOT_COUNT, CENTRAL_SLOT_COUNT + SUB_COLONY_SLOT_LIMIT, false)
                && !moveItemStackTo(moving, 0, CENTRAL_SLOT_COUNT, false)) {
            return ItemStack.EMPTY;
        }
        if (moving.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return original;
    }

    @Override
    public boolean stillValid(Player player) {
        if (player.level().isClientSide()) {
            return true;
        }
        return player.level().getBlockEntity(queenPos) instanceof QueenChamberBlockEntity
                && player.distanceToSqr(queenPos.getX() + 0.5, queenPos.getY() + 0.5, queenPos.getZ() + 0.5) <= 64.0;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id < 1000) {
            return false;
        }
        setScrollRow(id - 1000);
        return true;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!player.level().isClientSide() && companionCrow != null) {
            companionCrow.returnToOwner();
        }
    }

    private static final class DynamicSlot extends Slot {
        private final RavenTerminalMenu menu;

        private DynamicSlot(Container container, int slot, int x, int y, RavenTerminalMenu menu) {
            super(container, slot, x, y);
            this.menu = menu;
        }

        @Override
        public boolean isActive() {
            return getContainerSlot() + menu.getScrollRow() * 9 < menu.getSubColonySlotCount();
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return isActive() && super.mayPlace(stack);
        }

        @Override
        public boolean mayPickup(Player player) {
            return isActive() && super.mayPickup(player);
        }
    }
}
