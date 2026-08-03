package com.zengyj.exposer.blockentity;

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import com.zengyj.exposer.Registry;
import com.zengyj.exposer.node.NetworkNodeBridge;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collection;
import java.util.Collections;

public class RSBridgeBlockEntity extends NetworkNodeBlockEntity<NetworkNodeBridge> {

    public RSBridgeBlockEntity(BlockPos pos, BlockState state) {
        super(Registry.RS_BRIDGE_TYPE.get(), pos, state,ExposerBlockEntity.SPEC);
    }

    @Override
    public NetworkNodeBridge createNode(Level level, BlockPos blockPos) {
        return new NetworkNodeBridge(level,blockPos);
    }

    /**
     * 获取 RS 网络中所有可用的合成样板
     */
    public Collection<ICraftingPattern> getRsPatterns() {
        if (getNode().getNetwork() != null && getNode().getNetwork().canRun()) {
            return getNode().getNetwork().getCraftingManager().getPatterns();
        }
        return Collections.emptyList();
    }

    /**
     * 向 RS 网络请求合成指定物品
     */
    public boolean requestRSCrafting(ItemStack stack, int amount) {
        if (getNode().getNetwork() != null && getNode().getNetwork().canRun()) {
            return getNode().getNetwork().getCraftingManager().request(this, stack, amount) != null;
        }
        return false;
    }
}
