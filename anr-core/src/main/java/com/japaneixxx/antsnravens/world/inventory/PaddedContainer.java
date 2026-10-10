package com.japaneixxx.antsnravens.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;

public final class PaddedContainer implements Container {
    private final Container delegate;
    private final SimpleContainer padding;
    private final int delegateSize;
    private final int size;

    public PaddedContainer(Container delegate, int size) {
        this.delegate = delegate;
        this.delegateSize = delegate.getContainerSize();
        this.size = size;
        this.padding = new SimpleContainer(Math.max(0, size - delegateSize));
    }

    private boolean delegated(int slot) {
        return slot >= 0 && slot < delegateSize;
    }

    private int paddingSlot(int slot) {
        return slot - delegateSize;
    }

    @Override public int getContainerSize() { return size; }
    @Override public boolean isEmpty() { return delegate.isEmpty() && padding.isEmpty(); }
    @Override public ItemStack getItem(int slot) {
        return delegated(slot) ? delegate.getItem(slot) : padding.getItem(paddingSlot(slot));
    }
    @Override public ItemStack removeItem(int slot, int amount) {
        return delegated(slot) ? delegate.removeItem(slot, amount) : padding.removeItem(paddingSlot(slot), amount);
    }
    @Override public ItemStack removeItemNoUpdate(int slot) {
        return delegated(slot) ? delegate.removeItemNoUpdate(slot) : padding.removeItemNoUpdate(paddingSlot(slot));
    }
    @Override public void setItem(int slot, ItemStack stack) {
        if (delegated(slot)) delegate.setItem(slot, stack);
        else padding.setItem(paddingSlot(slot), stack);
    }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) {
        return delegated(slot) && delegate.canPlaceItem(slot, stack);
    }
    @Override public void setChanged() { delegate.setChanged(); padding.setChanged(); }
    @Override public boolean stillValid(Player player) { return delegate.stillValid(player); }
    @Override public void clearContent() { delegate.clearContent(); padding.clearContent(); }
}
