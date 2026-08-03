package com.zengyj.exposer.node;

import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class NetworkNodeBridge extends NetworkNode {
    public static final String ID = "bridge";

    public NetworkNodeBridge(Level level, BlockPos pos) {
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
}
