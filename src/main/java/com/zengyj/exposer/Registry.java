package com.zengyj.exposer;

import com.zengyj.exposer.block.BlockExposer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Constants.Mod_ID)
public class Registry {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Constants.Mod_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Constants.Mod_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Constants.Mod_ID);

    public static final RegistryObject<Block> EXPOSER = BLOCKS.register("exposer", BlockExposer::new);
    public static final RegistryObject<Item> EXPOSER_ITEM = ITEMS.register("exposer", () -> new BlockItem(EXPOSER.get(),new Item.Properties()));

    public static final RegistryObject<CreativeModeTab> TAB = CREATIVE_TABS.register("exposer", ()->
            CreativeModeTab.builder().withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                    .icon(()-> ItemStack.EMPTY).displayItems((itemDisplayParameters, output) -> {
                        output.accept(EXPOSER_ITEM.get());
                    }).build());

    public Registry() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        CREATIVE_TABS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }


}
