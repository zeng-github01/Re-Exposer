package com.zengyj.exposer;

import com.zengyj.exposer.block.BlockExposer;
import com.zengyj.exposer.blockentity.ExposerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class Registry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, Constants.Mod_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, Constants.Mod_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constants.Mod_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Constants.Mod_ID);

    public static final DeferredHolder<Block, Block> EXPOSER = BLOCKS.register("exposer", BlockExposer::new);
    public static final DeferredHolder<Item, Item> EXPOSER_ITEM = ITEMS.register("exposer", () -> new BlockItem(EXPOSER.get(), new Item.Properties()));

    public static final Supplier<BlockEntityType<ExposerBlockEntity>> EXPOSER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
            "exposer_block_entity",
            () -> BlockEntityType.Builder.of(ExposerBlockEntity::new, EXPOSER.get()).build(null)
    );
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = CREATIVE_TABS.register("exposer", () ->
            CreativeModeTab.builder()
                    .icon(() -> new ItemStack(EXPOSER.get())).displayItems((itemDisplayParameters, output) -> {
                        output.accept(EXPOSER_ITEM.get());
                    }).title(Component.translatable("exposer.modid")).build());
}
