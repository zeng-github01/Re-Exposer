package com.zengyj.exposer.capabilities;

import com.raoulvdberge.refinedstorage.tile.TileController;
import com.raoulvdberge.refinedstorage.tile.TileExternalStorage;
import com.zengyj.exposer.handler.FluidHandler;
import com.zengyj.exposer.handler.ItemHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ControllerCapability implements ICapabilityProvider {
    private TileController controller;
    private ItemHandler itemHandler;
    private FluidHandler fluidHandler;

    public ControllerCapability(TileController controller){ this.controller = controller;}

    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        if (facing != null) {
            BlockPos pos = this.controller.getPos().offset(facing);
            if (this.controller.getWorld().isBlockLoaded(pos)) {
                TileEntity te = this.controller.getWorld().getTileEntity(pos);
                if (te instanceof TileExternalStorage) {
                    return false;
                }
            }
        }

        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (this.itemHandler != null) {
                return true;
            }

            this.itemHandler = new ItemHandler(this.controller);
            this.controller.getItemStorageCache().addListener(this.itemHandler);
            return true;
        }

        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){

            if (this.fluidHandler != null) {
                return true;
            }

            this.fluidHandler = new FluidHandler(this.controller);
            this.controller.getFluidStorageCache().addListener(this.fluidHandler);
            return true;
        }

        return false;
    }

    @Nullable
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (!this.hasCapability(capability,facing)) return null;

        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.itemHandler);
        }

        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this.fluidHandler);
        }

        return null;
    }
}
