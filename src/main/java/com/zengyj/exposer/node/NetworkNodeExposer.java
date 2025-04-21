package com.zengyj.exposer.node;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.impl.node.SimpleNetworkNode;
import com.refinedmods.refinedstorage.api.network.node.NetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.zengyj.exposer.block.BlockExposer;
import com.zengyj.exposer.handler.FluidHandler;
import com.zengyj.exposer.handler.ItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;

public class NetworkNodeExposer extends SimpleNetworkNode {

    private ItemHandler itemHandler;
    private FluidHandler fluidHandler;
    public static final String ID = "exposer";

    public NetworkNodeExposer(long energyUsage) {
        super(energyUsage);
    }


}
