package com.japaneixxx.antsnravens.client.gui;

import com.japaneixxx.antsnravens.world.inventory.RavenTerminalMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class RavenTerminalScreen extends AbstractContainerScreen<RavenTerminalMenu> {
    private static final int SLOT_SIZE = 18;
    private static final int GRID_X = 8;
    private static final int CENTRAL_GRID_Y = 30;
    private static final int SUB_GRID_Y = 82;
    private static final int PLAYER_GRID_Y = 156;
    private static final int HOTBAR_Y = 210;
    private static final int SCROLLBAR_X = 178;
    private static final int SCROLLBAR_TOP = 82;
    private static final int SCROLLBAR_BOTTOM = 136;
    private boolean draggingScrollbar;

    public RavenTerminalScreen(RavenTerminalMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 196, 230);
    }

    @Override
    public void extractContents(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick) {
        extractor.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFF241D2B);
        extractor.fill(leftPos + 4, topPos + 20, leftPos + imageWidth - 4, topPos + 22, 0xFF6F4B78);
        extractor.text(font, title, leftPos + 8, topPos + 7, 0xFFFFFFFF);
        extractor.text(font, Component.translatable("screen.antsnravens.queen_inventory"),
                leftPos + 8, topPos + 22, 0xFFFFD37A);
        extractor.text(font, Component.translatable("screen.antsnravens.sub_colony_inventory",
                menu.getConnectedSubColonyCount()), leftPos + 8, topPos + 76, 0xFFB8E986);
        extractor.text(font, Component.translatable("screen.antsnravens.connected_chests",
                menu.getConnectedChestCount(), menu.getSubColonySlotCount()),
                leftPos + 8, topPos + 140, 0xFFD7C5E0);
        drawSlotBackgrounds(extractor);
        extractor.fill(leftPos + SCROLLBAR_X, topPos + SCROLLBAR_TOP,
                leftPos + SCROLLBAR_X + 8, topPos + SCROLLBAR_BOTTOM, 0xFF17131C);
        int trackHeight = SCROLLBAR_BOTTOM - SCROLLBAR_TOP - 8;
        int thumbY = SCROLLBAR_TOP + (menu.getMaxScrollRow() == 0
                ? 0 : trackHeight * menu.getScrollRow() / menu.getMaxScrollRow());
        extractor.fill(leftPos + SCROLLBAR_X, topPos + thumbY,
                leftPos + SCROLLBAR_X + 8, topPos + thumbY + 8, 0xFFB8E986);
        for (int tab = 0; tab < menu.getConnectedSubColonyCount(); tab++) {
            int tabX = leftPos + imageWidth + 2;
            int tabY = topPos + tab * 22;
            extractor.fill(tabX, tabY, tabX + 28, tabY + 20, 0xFF4B3154);
            extractor.text(font, Integer.toString(tab + 1), tabX + 11, tabY + 6, 0xFFFFFFFF);
        }
        super.extractContents(extractor, mouseX, mouseY, partialTick);
        extractor.fill(leftPos + 4, topPos + 140, leftPos + imageWidth - 4, topPos + 154, 0xFF241D2B);
        extractor.text(font, Component.translatable("screen.antsnravens.connected_chests",
                menu.getConnectedChestCount(), menu.getSubColonySlotCount()),
                leftPos + 8, topPos + 141, 0xFFD7C5E0);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        if (mouseX >= leftPos + 4 && mouseX <= leftPos + SCROLLBAR_X + 10
                && mouseY >= topPos + 68 && mouseY <= topPos + 150) {
            setScrollRow(menu.getScrollRow() - (int) Math.signum(deltaY));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, deltaX, deltaY);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0 && isOnScrollbar(event.x(), event.y())) {
            draggingScrollbar = true;
            updateScrollFromMouse(event.y());
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (draggingScrollbar && event.button() == 0) {
            updateScrollFromMouse(event.y());
            return true;
        }
        return super.mouseDragged(event, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (event.button() == 0) {
            draggingScrollbar = false;
        }
        return super.mouseReleased(event);
    }

    private boolean isOnScrollbar(double mouseX, double mouseY) {
        return mouseX >= leftPos + SCROLLBAR_X && mouseX <= leftPos + SCROLLBAR_X + 8
                && mouseY >= topPos + SCROLLBAR_TOP && mouseY <= topPos + SCROLLBAR_BOTTOM;
    }

    private void updateScrollFromMouse(double mouseY) {
        double fraction = (mouseY - topPos - SCROLLBAR_TOP - 4)
                / (double) (SCROLLBAR_BOTTOM - SCROLLBAR_TOP - 8);
        menu.setScrollFraction(fraction);
        setScrollRow(menu.getScrollRow());
    }

    private void drawSlotBackgrounds(GuiGraphicsExtractor extractor) {
        drawGrid(extractor, 3, CENTRAL_GRID_Y, 0xFF3A3042);
        for (int row = 0; row < 4; row++) {
            for (int column = 0; column < 9; column++) {
                int slot = row * 9 + column;
                int color = slot < menu.getSubColonySlotCount() ? 0xFF3A3042 : 0xFF211C27;
                drawSlot(extractor, GRID_X + column * SLOT_SIZE, SUB_GRID_Y + row * SLOT_SIZE, color);
            }
        }
        drawGrid(extractor, 3, PLAYER_GRID_Y, 0xFF30283A);
        drawGrid(extractor, 1, HOTBAR_Y, 0xFF30283A);
    }

    private void drawGrid(GuiGraphicsExtractor extractor, int rows, int y, int color) {
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < 9; column++) {
                drawSlot(extractor, GRID_X + column * SLOT_SIZE, y + row * SLOT_SIZE, color);
            }
        }
    }

    private void drawSlot(GuiGraphicsExtractor extractor, int x, int y, int color) {
        extractor.fill(leftPos + x, topPos + y, leftPos + x + 17, topPos + y + 17, 0xFF120F16);
        extractor.fill(leftPos + x + 1, topPos + y + 1, leftPos + x + 16, topPos + y + 16, color);
    }

    private void setScrollRow(int row) {
        menu.setScrollRow(row);
        if (minecraft != null && minecraft.gameMode != null && menu.getMaxScrollRow() > 0) {
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 1000 + menu.getScrollRow());
        }
    }
}
