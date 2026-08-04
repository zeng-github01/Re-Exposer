package com.zengyj.exposer.node;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.zengyj.exposer.blockentity.RSBridgeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

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
        return ResourceLocation.parse(ID);
    }

    @Override
    public void onConnected(INetwork network) {
        super.onConnected(network);
        BlockEntity blockEntity = this.level.getBlockEntity(pos);
        if (blockEntity instanceof RSBridgeBlockEntity bridge) {
            bridge.registerRSListener();
        }
    }

    @Override
    public void onDisconnected(INetwork network) {
        super.onDisconnected(network);
        BlockEntity blockEntity = this.level.getBlockEntity(pos);
        if (blockEntity instanceof RSBridgeBlockEntity bridge) {
            bridge.unregisterRSListener();
        }
    }
}
