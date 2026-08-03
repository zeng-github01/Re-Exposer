package com.zengyj.exposer.blockentity;

import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationSpec;
import com.zengyj.exposer.Registry;
import com.zengyj.exposer.node.NetworkNodeExposer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;


public class ExposerBlockEntity extends NetworkNodeBlockEntity<NetworkNodeExposer> {

    private NonNullSupplier<IItemHandler> itemSupplier;
    private NonNullSupplier<IFluidHandler> fluidSupplier;

    public static BlockEntitySynchronizationSpec SPEC;

    public ExposerBlockEntity(BlockPos pos, BlockState state) {
        super(Registry.EXPOSER_TYPE.get(), pos, state, SPEC);
    }


    @NonNull
    @Override
    public <T> LazyOptional<T> getCapability(@NonNull Capability<T> cap, @Nullable Direction direction) {
        if (cap == ForgeCapabilities.ITEM_HANDLER){
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

        if (cap == ForgeCapabilities.FLUID_HANDLER){
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

    static {
        SPEC = BlockEntitySynchronizationSpec.builder().build();
    }
}
