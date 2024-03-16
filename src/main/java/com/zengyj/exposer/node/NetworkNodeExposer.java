package com.zengyj.exposer.node;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNode;
import com.zengyj.exposer.handler.FluidHandler;
import com.zengyj.exposer.handler.ItemHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;

public class NetworkNodeExposer extends NetworkNode {

    private ItemHandler itemHandler;
    private FluidHandler fluidHandler;
    public static final String ID = "exposer";

    public NetworkNodeExposer(World world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public int getEnergyUsage() {
        return 1;
    }

    @Override
    public String getId() {
        return ID;
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

    @Override
    protected void onConnectedStateChange(INetwork network, boolean state) {
        super.onConnectedStateChange(network, state);
    }

    public IItemHandler getItemHandler() {
        return this.itemHandler;
    }
    public IFluidHandler getFluidHandler() {
        return this.fluidHandler;
    }
}
