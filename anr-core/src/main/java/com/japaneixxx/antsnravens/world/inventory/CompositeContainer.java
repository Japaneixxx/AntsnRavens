package com.japaneixxx.antsnravens.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class CompositeContainer implements Container {
    private final List<Container> containers;
    private final int size;

    public CompositeContainer(List<Container> containers) {
        this.containers = List.copyOf(containers);
        this.size = this.containers.stream().mapToInt(Container::getContainerSize).sum();
    }

    private SlotRef resolve(int slot) {
        int offset = slot;
        for (Container container : containers) {
            if (offset < container.getContainerSize()) {
                return new SlotRef(container, offset);
            }
            offset -= container.getContainerSize();
        }
        throw new IndexOutOfBoundsException("Invalid composite inventory slot: " + slot);
    }

    @Override
    public int getContainerSize() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return containers.stream().allMatch(Container::isEmpty);
    }

    @Override
    public ItemStack getItem(int slot) {
        SlotRef ref = resolve(slot);
        return ref.container().getItem(ref.slot());
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        SlotRef ref = resolve(slot);
        return ref.container().removeItem(ref.slot(), amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        SlotRef ref = resolve(slot);
        return ref.container().removeItemNoUpdate(ref.slot());
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        SlotRef ref = resolve(slot);
        ref.container().setItem(ref.slot(), stack);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        SlotRef ref = resolve(slot);
        return ref.container().canPlaceItem(ref.slot(), stack);
    }

    @Override
    public void setChanged() {
        containers.forEach(Container::setChanged);
    }

    @Override
    public boolean stillValid(net.minecraft.world.entity.player.Player player) {
        return containers.stream().allMatch(container -> container.stillValid(player));
    }

    @Override
    public void clearContent() {
        containers.forEach(Container::clearContent);
    }

    private record SlotRef(Container container, int slot) {
    }
}
