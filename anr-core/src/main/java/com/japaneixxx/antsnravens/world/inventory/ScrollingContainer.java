package com.japaneixxx.antsnravens.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class ScrollingContainer implements Container {
    private final Container delegate;
    private final int visibleSize;
    private int offset;

    public ScrollingContainer(Container delegate, int visibleSize) {
        this.delegate = delegate;
        this.visibleSize = visibleSize;
    }

    public void setOffset(int offset) {
        this.offset = Math.max(0, Math.min(offset, Math.max(0, delegate.getContainerSize() - visibleSize)));
    }

    private int resolve(int slot) {
        if (slot < 0 || slot >= visibleSize) {
            throw new IndexOutOfBoundsException("Invalid scrolling inventory slot: " + slot);
        }
        return offset + slot;
    }

    @Override public int getContainerSize() { return visibleSize; }
    @Override public boolean isEmpty() { return delegate.isEmpty(); }
    @Override public ItemStack getItem(int slot) { return delegate.getItem(resolve(slot)); }
    @Override public ItemStack removeItem(int slot, int amount) { return delegate.removeItem(resolve(slot), amount); }
    @Override public ItemStack removeItemNoUpdate(int slot) { return delegate.removeItemNoUpdate(resolve(slot)); }
    @Override public void setItem(int slot, ItemStack stack) { delegate.setItem(resolve(slot), stack); }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) {
        return delegate.canPlaceItem(resolve(slot), stack);
    }
    @Override public void setChanged() { delegate.setChanged(); }
    @Override public boolean stillValid(Player player) { return delegate.stillValid(player); }
    @Override public void clearContent() { delegate.clearContent(); }
}
