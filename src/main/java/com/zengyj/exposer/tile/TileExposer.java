package com.zengyj.exposer.tile;

import com.raoulvdberge.refinedstorage.tile.TileNode;
import com.zengyj.exposer.node.NetworkNodeExposer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileExposer extends TileNode<NetworkNodeExposer> {
    @Override
    public NetworkNodeExposer createNode(World world, BlockPos blockPos) {
        return new NetworkNodeExposer(world,blockPos);
    }

    @Override
    public String getNodeId() {
        return NetworkNodeExposer.ID;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing side) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            if (getNode().getItemHandler() != null) return true;
        }

        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
            if (getNode().getFluidHandler() != null) return true;
        }

        return super.hasCapability(capability, side);
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing side) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && getNode().getItemHandler() != null){
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(getNode().getItemHandler());
        }

        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && getNode().getFluidHandler() != null){
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(getNode().getFluidHandler());
        }

        return super.getCapability(capability, side);
    }
}
