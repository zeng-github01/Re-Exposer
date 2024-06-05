package com.zengyj.exposer;


import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Constants.Mod_ID)
public class Exposer {
    public static final Logger LOGGER  = LogManager.getLogger(Constants.Mod_ID);
    public static Registry REGISTRY;
    public Exposer(){
        REGISTRY = new Registry();
    }
}