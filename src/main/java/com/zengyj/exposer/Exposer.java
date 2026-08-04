package com.zengyj.exposer;


import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

@Mod(Constants.Mod_ID)
public class Exposer {
    public static final Logger LOGGER  = LogManager.getLogger(Constants.Mod_ID);

    public Exposer() {
        Registry.register();
    }
}