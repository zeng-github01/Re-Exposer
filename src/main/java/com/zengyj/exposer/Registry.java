package com.zengyj.exposer;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.tile.NetworkReceiverTile;
import com.zengyj.exposer.block.BlockExposer;
import com.zengyj.exposer.tile.TileExposer;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = Constants.Mod_ID)
public class Registry {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Constants.Mod_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Constants.Mod_ID);
    public static final DeferredRegister<TileEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Constants.Mod_ID);


    public static final RegistryObject<Block> EXPOSER = BLOCKS.register("exposer", BlockExposer::new);
    public static final RegistryObject<Item> EXPOSER_ITEM = ITEMS.register("exposer", () -> new BlockItem(EXPOSER.get(), new Item.Properties().group(com.refinedmods.refinedstorage.RS.MAIN_GROUP)));

    public static final RegistryObject<TileEntityType<TileExposer>> EXPOSER_TYPE = BLOCK_ENTITY_TYPES.register("requester", () -> TileEntityType.Builder
            .create(TileExposer::new, Registry.EXPOSER.get())
            .build(null));

    public Registry() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        BLOCK_ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
