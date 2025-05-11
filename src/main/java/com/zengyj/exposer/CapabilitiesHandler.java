package com.zengyj.exposer;

import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.neoforge.api.RefinedStorageNeoForgeApi;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = Constants.Mod_ID, bus = EventBusSubscriber.Bus.MOD)
public class CapabilitiesHandler {
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                Registry.EXPOSER_BLOCK_ENTITY.get(),
                (blockEntity, context) -> blockEntity.getItemHandler(context) // context 即 Direction (朝向)
        );

        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                Registry.EXPOSER_BLOCK_ENTITY.get(),
                (blockEntity, context) -> blockEntity.getFluidHandler(context) // context 即 Direction (朝向)
        );

        event.registerBlockEntity(
                RefinedStorageNeoForgeApi.INSTANCE.getNetworkNodeContainerProviderCapability(),
                Registry.EXPOSER_BLOCK_ENTITY.get(),
                (blockEntity, side) -> blockEntity.getContainerProvider()
        );
    }
}
