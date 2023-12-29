package com.zengyj.exposer.util;

import com.raoulvdberge.refinedstorage.tile.TileController;
import com.raoulvdberge.refinedstorage.tile.grid.TileGrid;
import com.zengyj.exposer.Constants;
import com.zengyj.exposer.capabilities.ControllerCapability;
import com.zengyj.exposer.capabilities.GridCapability;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Constants.Mod_ID)
public class EventListener {

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<TileEntity> event) {
        if (event.getObject() instanceof TileController && ModConfig.expose) {
            event.addCapability(new ResourceLocation("exposer", "coreexpose"), new ControllerCapability((TileController)event.getObject()));
        } else if (event.getObject() instanceof TileGrid && ModConfig.gridExpose && ModConfig.expose) {
            event.addCapability(new ResourceLocation("exposer", "gridexpose"), new GridCapability((TileGrid)event.getObject()));
        }

    }
}
