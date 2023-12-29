package com.zengyj.exposer.util;

import com.refinedmods.refinedstorage.tile.ControllerTile;
import com.refinedmods.refinedstorage.tile.grid.GridTile;
import com.zengyj.exposer.Constants;
import com.zengyj.exposer.capabilities.ControllerCapability;
import com.zengyj.exposer.capabilities.GridCapability;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Constants.Mod_ID)
public class EventListener {

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<TileEntity> event) {
        if (event.getObject() instanceof ControllerTile && ModConfig.expose) {
            event.addCapability(new ResourceLocation("exposer", "coreexpose"), new ControllerCapability((ControllerTile) event.getObject()));
        } else if (event.getObject() instanceof GridTile && ModConfig.gridExpose && ModConfig.expose) {
            event.addCapability(new ResourceLocation("exposer", "gridexpose"), new GridCapability((GridTile) event.getObject()));
        }

    }
}
