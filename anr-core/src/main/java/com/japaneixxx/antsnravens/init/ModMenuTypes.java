package com.japaneixxx.antsnravens.init;

import com.japaneixxx.antsnravens.AntsNRavensCore;
import com.japaneixxx.antsnravens.world.inventory.RavenTerminalMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, AntsNRavensCore.MOD_ID);
    public static final DeferredHolder<MenuType<?>, MenuType<RavenTerminalMenu>> RAVEN_TERMINAL =
            MENUS.register("raven_terminal",
                    () -> IMenuTypeExtension.create(RavenTerminalMenu::new));

    private ModMenuTypes() {
    }
}
