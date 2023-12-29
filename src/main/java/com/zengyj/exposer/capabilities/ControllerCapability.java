package com.zengyj.exposer.capabilities;

import com.raoulvdberge.refinedstorage.tile.TileController;
import com.raoulvdberge.refinedstorage.tile.TileExternalStorage;
import com.zengyj.exposer.handler.ItemHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ControllerCapability implements ICapabilityProvider {
    private TileController controller;
    private ItemHandler itemHandler;

    public ControllerCapability(TileController controller){ this.controller = controller;}

    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (facing != null) {
                BlockPos pos = this.controller.getPos().offset(facing);
                if (this.controller.getWorld().isBlockLoaded(pos)) {
                    TileEntity te = this.controller.getWorld().getTileEntity(pos);
                    if (te instanceof TileExternalStorage) {
                        return false;
                    }
                }
            }

            if (this.itemHandler != null) {
                return true;
            }

            if (this.controller.getItemStorageCache() != null) {
                this.itemHandler = new ItemHandler(this.controller);
                this.controller.getItemStorageCache().addListener(this.itemHandler);
                return true;
            }
        }

        return false;
    }

    @Nullable
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return this.hasCapability(capability, facing) ? CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.itemHandler) : null;
    }
}
