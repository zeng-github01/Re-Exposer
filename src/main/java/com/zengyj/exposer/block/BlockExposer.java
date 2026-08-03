package com.zengyj.exposer.block;

import com.refinedmods.refinedstorage.block.BlockDirection;
import com.refinedmods.refinedstorage.block.NetworkNodeBlock;
import com.refinedmods.refinedstorage.util.BlockUtils;
import com.zengyj.exposer.blockentity.ExposerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class BlockExposer extends NetworkNodeBlock {

    public BlockExposer() {
        super(BlockUtils.DEFAULT_ROCK_PROPERTIES);
    }

    public BlockDirection getDirection() {
        return BlockDirection.NONE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ExposerBlockEntity(blockPos, blockState);
    }

    @Override
    public boolean hasConnectedState() {
        return true;
    }
}
