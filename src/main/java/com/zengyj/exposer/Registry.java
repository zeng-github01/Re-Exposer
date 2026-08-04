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
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Constants.Mod_ID)
public class Registry {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Constants.Mod_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Constants.Mod_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Constants.Mod_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Constants.Mod_ID);

    public static final RegistryObject<Block> EXPOSER = BLOCKS.register("exposer", BlockExposer::new);
    public static final RegistryObject<Item> EXPOSER_ITEM = ITEMS.register("exposer", () -> new BlockItem(EXPOSER.get(), new Item.Properties()));

    public static final RegistryObject<Block> RS_BRIDGE = BLOCKS.register("rs_bridge", RSBridgeBlock::new);
    public static final RegistryObject<Item> RS_BRIDGE_ITEM = ITEMS.register("rs_bridge", () -> new BlockItem(RS_BRIDGE.get(), new Item.Properties()));

    public static final RegistryObject<Block> AE_BRIDGE = BLOCKS.register("ae_bridge", AEBridgeBlock::new);
    public static final RegistryObject<Item> AE_BRIDGE_ITEM = ITEMS.register("ae_bridge", () -> new BlockItem(AE_BRIDGE.get(), new Item.Properties()));

    public static final RegistryObject<CreativeModeTab> TAB = CREATIVE_TABS.register("exposer", () ->
            CreativeModeTab.builder().withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                    .icon(() -> new ItemStack(EXPOSER.get())).displayItems((itemDisplayParameters, output) -> {
                        output.accept(EXPOSER_ITEM.get());
                        output.accept(RS_BRIDGE_ITEM.get());
                        output.accept(AE_BRIDGE_ITEM.get());
                    }).title(Component.translatable("exposer.modid")).build());

    public static final RegistryObject<BlockEntityType<ExposerBlockEntity>> EXPOSER_TYPE = BLOCK_ENTITY_TYPES.register("exposer", () -> BlockEntityType.Builder
            .of(ExposerBlockEntity::new, Registry.EXPOSER.get())
            .build(null));

    public static final RegistryObject<BlockEntityType<RSBridgeBlockEntity>> RS_BRIDGE_TYPE = BLOCK_ENTITY_TYPES.register("rs_bridge", () -> BlockEntityType.Builder
            .of(RSBridgeBlockEntity::new, Registry.RS_BRIDGE.get())
            .build(null));

    public static final RegistryObject<BlockEntityType<AEBridgeBlockEntity>> AE_BRIDGE_TYPE = BLOCK_ENTITY_TYPES.register("ae_bridge", () -> {
        BlockEntityType<AEBridgeBlockEntity> type = BlockEntityType.Builder
                .of(AEBridgeBlockEntity::new, AE_BRIDGE.get())
                .build(null);
        ((AEBridgeBlock) AE_BRIDGE.get()).setBlockEntity(
                AEBridgeBlockEntity.class,
                type,
                null,
                (level, pos, state, entity) -> entity.serverTick()
        );

        return type;
    });

    // 💡 1. 注册 DeferredRegister 到事件总线
    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_TABS.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);

        // 💡 监听 CommonSetup 事件
        modEventBus.addListener(Registry::commonSetup);
    }

    // 💡 2. 在 CommonSetup 阶段完成 AE2 的绑定
    private static void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // 向 AE2 注册 BlockEntityType 与 Item 的映射
            AEBaseBlockEntity.registerBlockEntityItem(
                    AE_BRIDGE_TYPE.get(),
                    AE_BRIDGE_ITEM.get()
            );
        });
    }
}
