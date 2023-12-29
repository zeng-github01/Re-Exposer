package com.zengyj.exposer.util;

import com.zengyj.exposer.Constants;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.common.ForgeConfigSpec;

public class ModConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.ConfigValue<Boolean> expose;
    public static final ForgeConfigSpec.ConfigValue<Boolean> gridExpose;

    static {
        BUILDER.push("Config for Re-Exposer");
        expose = BUILDER.comment("Enables the Controller to expose the network.").define("Enable Exposing",true);
        gridExpose = BUILDER.comment("Should the Grid expose the networks contents? (requires Enable Exposing to be true)").define("Grid Expose",true);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
