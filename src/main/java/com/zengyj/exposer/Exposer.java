package com.zengyj.exposer;



import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Constants.Mod_ID)
public class Exposer {
    public static final Logger LOGGER  = LogManager.getLogger(Constants.Mod_ID);

    public Exposer(FMLJavaModLoadingContext context) {
        Registry.register(context.getModEventBus());
    }
}