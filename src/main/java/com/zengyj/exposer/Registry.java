package com.zengyj.exposer;

import com.raoulvdberge.refinedstorage.RS;
import com.zengyj.exposer.block.BlockExposer;
import com.zengyj.exposer.tile.TileExposer;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber(modid = Constants.Mod_ID)
public class Registry {

    public static final BlockExposer EXPOSER = new BlockExposer();
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(EXPOSER);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new ItemBlock(EXPOSER).setRegistryName(Constants.Mod_ID, "exposer"));

    }
}
