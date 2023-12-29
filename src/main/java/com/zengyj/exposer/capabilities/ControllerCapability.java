package com.zengyj.exposer.capabilities;

import com.refinedmods.refinedstorage.tile.ControllerTile;
import com.refinedmods.refinedstorage.tile.ExternalStorageTile;
import com.zengyj.exposer.handler.ItemHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.items.CapabilityItemHandler;
import org.omg.CORBA.PRIVATE_MEMBER;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ControllerCapability implements ICapabilityProvider {
    private ControllerTile controller;

    private NonNullSupplier<ItemHandler> supplier;
    private ItemHandler itemHandler;

    public ControllerCapability(ControllerTile controller){ this.controller = controller;}

    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable Direction direction) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (direction != null) {
                BlockPos pos = this.controller.getBlockPos().offset(direction.getNormal());
                if (this.controller.getLevel().isLoaded(pos)) {
                    TileEntity te = this.controller.getLevel().getBlockEntity(pos);
                    if (te instanceof ExternalStorageTile) {
                        return false;
                    }
                }
            }

            if (this.itemHandler != null) {
                return true;
            }

            if (this.controller.getNetwork().getItemStorageCache() != null) {
                this.itemHandler = new ItemHandler(this.controller.getNetwork());
                this.controller.getNetwork().getItemStorageCache().addListener(this.itemHandler);
                return true;
            }
        }

        return false;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction direction) {
//        return this.hasCapability(capability, direction) ? LazyOptional.of(itemHandler).cast() : null;

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

            if (this.itemHandler != null) {
                supplier = new NonNullSupplier<ItemHandler>() {
                    @Nonnull
                    @Override
                    public ItemHandler get() {
                        return itemHandler;
                    }
                };

                return (LazyOptional<T>) LazyOptional.of(supplier);
            }

            if (this.controller.getNetwork().getItemStorageCache() != null) {
                this.itemHandler = new ItemHandler(this.controller.getNetwork());
                this.controller.getNetwork().getItemStorageCache().addListener(this.itemHandler);
                supplier = new NonNullSupplier<ItemHandler>() {
                    @Nonnull
                    @Override
                    public ItemHandler get() {
                        return itemHandler;
                    }
                };

                return (LazyOptional<T>) LazyOptional.of(supplier);
            }
        }

        return LazyOptional.empty();
    }
}
