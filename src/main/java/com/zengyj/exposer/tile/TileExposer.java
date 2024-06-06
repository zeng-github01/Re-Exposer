package com.zengyj.exposer.tile;

import com.refinedmods.refinedstorage.RSTiles;
import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import com.zengyj.exposer.Registry;
import com.zengyj.exposer.handler.FluidHandler;
import com.zengyj.exposer.handler.ItemHandler;
import com.zengyj.exposer.node.NetworkNodeExposer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileExposer extends NetworkNodeTile<NetworkNodeExposer> {

    private NonNullSupplier<IItemHandler> itemSupplier;
    private NonNullSupplier<IFluidHandler> fluidSupplier;
    public TileExposer() {
        super(Registry.EXPOSER_TYPE.get());
    }

    @Override
    public NetworkNodeExposer createNode(World world, BlockPos blockPos) {
        return new NetworkNodeExposer(world,blockPos);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction direction) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            if (itemSupplier != null){
                return (LazyOptional<T>) LazyOptional.of(itemSupplier);
            }

            if (getNode().getItemHandler() == null){
                return LazyOptional.empty();
            }

            itemSupplier = new NonNullSupplier<IItemHandler>() {
                @Nonnull
                @Override
                public IItemHandler get() {
                    return getNode().getItemHandler();
                }
            };

            return (LazyOptional<T>) LazyOptional.of(itemSupplier);
        }

        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
            if (fluidSupplier != null){
                return (LazyOptional<T>) LazyOptional.of(fluidSupplier);
            }

            if (getNode().getFluidHandler() == null){
                return LazyOptional.empty();
            }

            fluidSupplier = new NonNullSupplier<IFluidHandler>() {
                @Nonnull
                @Override
                public IFluidHandler get() {
                    return getNode().getFluidHandler();
                }
            };

            return (LazyOptional<T>) LazyOptional.of(fluidSupplier);
        }

        return super.getCapability(cap, direction);
    }
}
