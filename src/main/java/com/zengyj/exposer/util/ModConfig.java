package com.zengyj.exposer.util;

import com.zengyj.exposer.Constants;
import net.minecraftforge.common.config.Config;

@Config(modid = Constants.Mod_ID)
public class ModConfig {

    @Config.Name("Grid Expose")
    @Config.Comment({"Should the Grid expose the networks contents? (requires Enable Exposing to be true)"})
    public static boolean gridExpose = true;
    @Config.Name("Enable Exposing")
    @Config.Comment({"Enables the Controller to expose the network."})
    public static boolean expose = true;
}
