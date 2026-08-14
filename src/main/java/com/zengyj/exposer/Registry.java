package com.zengyj.exposer;

import appeng.blockentity.AEBaseBlockEntity;
import com.zengyj.exposer.block.AEBridgeBlock;
import com.zengyj.exposer.block.BlockExposer;
import com.zengyj.exposer.block.RSBridgeBlock;
import com.zengyj.exposer.blockentity.AEBridgeBlockEntity;
import com.zengyj.exposer.blockentity.ExposerBlockEntity;
import com.zengyj.exposer.blockentity.RSBridgeBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Registry {

    private static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Constants.Mod_ID);
    private static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Constants.Mod_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Constants.Mod_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Constants.Mod_ID);

    public static final RegistryObject<Block> EXPOSER =
            BLOCKS.register("exposer", BlockExposer::new);
    public static final RegistryObject<Item> EXPOSER_ITEM =
            ITEMS.register("exposer", () -> new BlockItem(EXPOSER.get(), new Item.Properties()));

    public static final RegistryObject<BlockEntityType<ExposerBlockEntity>> EXPOSER_TYPE =
            BLOCK_ENTITY_TYPES.register("exposer", () ->
                    BlockEntityType.Builder
                            .of(ExposerBlockEntity::new, EXPOSER.get())
                            .build(null)
            );

    public static RegistryObject<Block> RS_BRIDGE;
    public static RegistryObject<Item> RS_BRIDGE_ITEM;

    public static RegistryObject<Block> AE_BRIDGE;
    public static RegistryObject<Item> AE_BRIDGE_ITEM;

    public static RegistryObject<BlockEntityType<RSBridgeBlockEntity>> RS_BRIDGE_TYPE;
    public static RegistryObject<BlockEntityType<AEBridgeBlockEntity>> AE_BRIDGE_TYPE;

    public static final RegistryObject<CreativeModeTab> TAB =
            CREATIVE_TABS.register("exposer", () ->
                    CreativeModeTab.builder()
                            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                            .icon(() -> new ItemStack(EXPOSER.get()))
                            .displayItems((params, output) -> {
                                // 永远有的
                                output.accept(EXPOSER_ITEM.get());

                                // 只有 AE2 存在且 bridge 已注册时才加入
                                if (RS_BRIDGE_ITEM != null) {
                                    output.accept(RS_BRIDGE_ITEM.get());
                                }
                                if (AE_BRIDGE_ITEM != null) {
                                    output.accept(AE_BRIDGE_ITEM.get());
                                }
                            })
                            .title(Component.translatable("exposer.modid"))
                            .build()
            );

    public static void register(IEventBus modEventBus) {
        if (ModList.get().isLoaded("ae2")) {
            registerBridges();
        }

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_TABS.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);

        modEventBus.addListener(Registry::commonSetup);
    }

    private static void registerBridges() {
        RS_BRIDGE = BLOCKS.register("rs_bridge", RSBridgeBlock::new);
        RS_BRIDGE_ITEM = ITEMS.register("rs_bridge",
                () -> new BlockItem(RS_BRIDGE.get(), new Item.Properties()));

        RS_BRIDGE_TYPE = BLOCK_ENTITY_TYPES.register("rs_bridge",
                () -> BlockEntityType.Builder.of(
                        RSBridgeBlockEntity::new,
                        RS_BRIDGE.get()
                ).build(null));

        AE_BRIDGE = BLOCKS.register("ae_bridge", AEBridgeBlock::new);
        AE_BRIDGE_ITEM = ITEMS.register("ae_bridge",
                () -> new BlockItem(AE_BRIDGE.get(), new Item.Properties()));

        AE_BRIDGE_TYPE = BLOCK_ENTITY_TYPES.register("ae_bridge", () -> {
            BlockEntityType<AEBridgeBlockEntity> type =
                    BlockEntityType.Builder.of(
                            AEBridgeBlockEntity::new,
                            AE_BRIDGE.get()
                    ).build(null);

            ((AEBridgeBlock) AE_BRIDGE.get()).setBlockEntity(
                    AEBridgeBlockEntity.class,
                    type,
                    null,
                    (level, pos, state, entity) -> entity.serverTick()
            );

            return type;
        });
    }

    private static void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // 只有 AE2 存在且 bridge 已注册时才做 AE2 的绑定
            if (ModList.get().isLoaded("ae2") && AE_BRIDGE_TYPE != null && AE_BRIDGE_ITEM != null) {
                AEBaseBlockEntity.registerBlockEntityItem(
                        AE_BRIDGE_TYPE.get(),
                        AE_BRIDGE_ITEM.get()
                );
            }
        });
    }
}

