package com.zengyj.exposer;


import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

@Mod(Constants.Mod_ID)
public class Exposer {
    public Exposer(IEventBus bus) {
        Registry.BLOCKS.register(bus);
        Registry.ITEMS.register(bus);
        Registry.CREATIVE_TABS.register(bus);
        Registry.BLOCK_ENTITY_TYPES.register(bus);
    }

    public static final Logger LOGGER = LogManager.getLogger(Constants.Mod_ID);
}