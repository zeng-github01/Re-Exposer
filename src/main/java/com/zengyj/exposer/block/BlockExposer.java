package com.zengyj.exposer.block;

import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.block.BlockDirection;
import com.refinedmods.refinedstorage.block.NetworkNodeBlock;
import com.refinedmods.refinedstorage.util.BlockUtils;
import com.zengyj.exposer.Constants;
import com.zengyj.exposer.tile.TileExposer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class BlockExposer extends NetworkNodeBlock {

    public BlockExposer() {
        super(BlockUtils.DEFAULT_ROCK_PROPERTIES);;
    }

    public BlockDirection getDirection() {
        return BlockDirection.HORIZONTAL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new TileExposer(blockPos, blockState);
    }


}
