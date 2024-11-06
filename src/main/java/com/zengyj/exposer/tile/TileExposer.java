package com.zengyj.exposer.tile;

import com.refinedmods.refinedstorage.RSBlockEntities;
import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import com.zengyj.exposer.Exposer;
import com.zengyj.exposer.Registry;
import com.zengyj.exposer.node.NetworkNodeExposer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;


public class TileExposer extends NetworkNodeBlockEntity<NetworkNodeExposer> {

    private NonNullSupplier<IItemHandler> itemSupplier;
    private NonNullSupplier<IFluidHandler> fluidSupplier;

    public TileExposer(BlockPos pos, BlockState state) {
        super(Registry.EXPOSER_TYPE.get(), pos, state);
    }


    @NonNull
    @Override
    public <T> LazyOptional<T> getCapability(@NonNull Capability<T> cap, @Nullable Direction direction) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            if (itemSupplier != null){
                return (LazyOptional<T>) LazyOptional.of(itemSupplier);
            }

            if (getNode().getItemHandler() == null){
                return LazyOptional.empty();
            }

            itemSupplier = new NonNullSupplier<>() {
                @NonNull
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

            fluidSupplier = new NonNullSupplier<>() {
                @NonNull
                @Override
                public IFluidHandler get() {
                    return getNode().getFluidHandler();
                }
            };

            return (LazyOptional<T>) LazyOptional.of(fluidSupplier);
        }

        return super.getCapability(cap, direction);
    }

    @Override
    public NetworkNodeExposer createNode(Level level, BlockPos blockPos) {
        return new NetworkNodeExposer(level,blockPos);
    }
}
