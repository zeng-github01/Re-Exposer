package com.zengyj.exposer.node;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.zengyj.exposer.handler.FluidHandler;
import com.zengyj.exposer.handler.ItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;

public class NetworkNodeExposer extends NetworkNode {

    private ItemHandler itemHandler;
    private FluidHandler fluidHandler;
    public static final String ID = "exposer";

    public NetworkNodeExposer(Level level, BlockPos pos) {
        super(level, pos);
    }

    @Override
    public int getEnergyUsage() {
        return 1;
    }

    @Override
    public ResourceLocation getId() {
        return new ResourceLocation(ID);
    }

    @Override
    public void onConnected(INetwork network) {
        super.onConnected(network);
        this.itemHandler = new ItemHandler(network);
        this.fluidHandler = new FluidHandler(network);
        network.getItemStorageCache().addListener(this.itemHandler);
        network.getFluidStorageCache().addListener(this.fluidHandler);
    }

    @Override
    public void onDisconnected(INetwork network) {
        super.onDisconnected(network);
        network.getItemStorageCache().removeListener(this.itemHandler);
        network.getFluidStorageCache().removeListener(this.fluidHandler);
    }

    public IItemHandler getItemHandler() {
        return this.itemHandler;
    }
    public IFluidHandler getFluidHandler() {
        return this.fluidHandler;
    }
}
