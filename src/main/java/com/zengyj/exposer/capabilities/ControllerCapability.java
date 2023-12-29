package com.zengyj.exposer.capabilities;

import com.refinedmods.refinedstorage.tile.ControllerTile;
import com.refinedmods.refinedstorage.tile.ExternalStorageTile;
import com.zengyj.exposer.handler.FluidHandler;
import com.zengyj.exposer.handler.ItemHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ControllerCapability implements ICapabilityProvider {
    private ControllerTile controller;

    private NonNullSupplier<ItemHandler> itemSupplier;
    private NonNullSupplier<FluidHandler> fluidSupplier;
    private ItemHandler itemHandler;
    private FluidHandler fluidHandler;

    public ControllerCapability(ControllerTile controller){ this.controller = controller;}

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction direction) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (direction != null) {
                BlockPos pos = this.controller.getBlockPos().offset(direction.getNormal());
                if (this.controller.getLevel().isLoaded(pos)) {
                    TileEntity te = this.controller.getLevel().getBlockEntity(pos);
                    if (te instanceof ExternalStorageTile) {
                        return LazyOptional.empty();
                    }
                }
            }

            if (this.itemSupplier != null) {
                return (LazyOptional<T>) LazyOptional.of(itemSupplier);
            }

            if (this.controller.getNetwork().getItemStorageCache() != null) {
                this.itemHandler = new ItemHandler(this.controller.getNetwork());
                this.controller.getNetwork().getItemStorageCache().addListener(this.itemHandler);
                itemSupplier = new NonNullSupplier<ItemHandler>() {
                    @Nonnull
                    @Override
                    public ItemHandler get() {
                        return itemHandler;
                    }
                };

                return (LazyOptional<T>) LazyOptional.of(itemSupplier);
            }
        }

        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
            if (direction != null) {
                BlockPos pos = this.controller.getBlockPos().offset(direction.getNormal());
                if (this.controller.getLevel().isLoaded(pos)) {
                    TileEntity te = this.controller.getLevel().getBlockEntity(pos);
                    if (te instanceof ExternalStorageTile) {
                        return LazyOptional.empty();
                    }
                }
            }

            if (this.fluidSupplier != null) {
                return (LazyOptional<T>) LazyOptional.of(fluidSupplier);
            }

            if (this.controller.getNetwork().getFluidStorageCache() != null) {
                this.fluidHandler = new FluidHandler(this.controller.getNetwork());
                this.controller.getNetwork().getFluidStorageCache().addListener(this.fluidHandler);
                fluidSupplier = new NonNullSupplier<FluidHandler>() {
                    @Nonnull
                    @Override
                    public FluidHandler get() {
                        return fluidHandler;
                    }
                };

                return (LazyOptional<T>) LazyOptional.of(fluidSupplier);
            }
        }

        return LazyOptional.empty();
    }
}
