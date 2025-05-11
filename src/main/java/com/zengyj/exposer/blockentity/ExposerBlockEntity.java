package com.zengyj.exposer.blockentity;

import com.refinedmods.refinedstorage.api.network.impl.node.SimpleNetworkNode;
import com.refinedmods.refinedstorage.common.support.network.AbstractBaseNetworkNodeContainerBlockEntity;
import com.zengyj.exposer.Registry;
import com.zengyj.exposer.content.ContentNames;
import com.zengyj.exposer.handler.FluidHandler;
import com.zengyj.exposer.handler.ItemHandler;
import com.zengyj.exposer.node.ExposerNetworkNode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.EmptyFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class ExposerBlockEntity extends AbstractBaseNetworkNodeContainerBlockEntity<ExposerNetworkNode> {
    public ExposerBlockEntity(BlockPos pos, BlockState state) {
        super(Registry.EXPOSER_BLOCK_ENTITY.get(), pos, state, new ExposerNetworkNode(1));
    }

    @Override
    public @NotNull Component getName() {
        return ContentNames.EXPOSER;
    }

    public IItemHandler getItemHandler(@Nullable Direction side) {
        if (!mainNetworkNode.isActive() || mainNetworkNode.getNetwork() == null || mainNetworkNode.itemHandler == null) {
            return EmptyItemHandler.INSTANCE;
        }
        return mainNetworkNode.itemHandler;
    }

    public IFluidHandler getFluidHandler(@Nullable Direction side) {
        if (!mainNetworkNode.isActive() || mainNetworkNode.getNetwork() == null || mainNetworkNode.fluidHandler == null) {
            return EmptyFluidHandler.INSTANCE;
        }
        return mainNetworkNode.fluidHandler;
    }
}
