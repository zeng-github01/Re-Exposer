package com.zengyj.exposer;

import com.zengyj.exposer.block.BlockExposer;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class Registry {

    public static BlockExposer EXPOSER;

    @SubscribeEvent
    public static void addItems(RegistryEvent.Register<Item> event) {
        if (ModConfig.exposer) {
            event.getRegistry().register((new ItemBlock(new BlockExposer())).setRegistryName(EXPOSER.getRegistryName()));
        }
    }

    @SubscribeEvent
    public static void addBlocks(RegistryEvent.Register<Block> event) {
        if (ModConfig.exposer) {
            event.getRegistry().register(EXPOSER = new BlockExposer());
        }

    }
}
