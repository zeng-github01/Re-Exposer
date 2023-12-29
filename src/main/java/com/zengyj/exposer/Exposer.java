package com.zengyj.exposer;

import net.minecraftforge.fml.common.Mod;

@Mod(
        modid = Constants.Mod_ID,
        version = Constants.Version,
        name = Constants.Mod_Name,
        dependencies = Constants.Dependencies)
public class Exposer {
    @Mod.Instance("exposer")
    public static Exposer INSTANCE;
}