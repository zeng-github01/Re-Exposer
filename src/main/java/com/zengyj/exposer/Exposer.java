package com.zengyj.exposer;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Constants.Mod_ID)
public class Exposer {

    public static final Logger LOGGER  = LogManager.getLogger(Constants.Mod_ID);
    public Exposer(){
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, com.zengyj.exposer.util.ModConfig.SPEC);
    }
}