package com.zengyj.exposer;

import net.minecraftforge.common.config.Config;

@Config(modid = Constants.Mod_ID)
public class ModConfig {

    @Config.Name("Grid Expose")
    @Config.Comment({"Should the Grid expose the networks contents? (requires Enable Exposing to be true)"})
    @Config.RequiresWorldRestart
    public static boolean gridExpose = true;
    @Config.Name("Enable Exposing")
    @Config.Comment({"Enables the Controller to expose the network."})
    @Config.RequiresWorldRestart
    public static boolean exposer = true;
}
