package com.zengyj.exposer;

import com.zengyj.exposer.block.BlockExposer;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
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