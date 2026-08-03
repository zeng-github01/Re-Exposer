package com.zengyj.exposer.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class BlockLinkUtil {
    /**
     * 检查当前方块正面是否连接了正确的对面方块，且对面的正面正好朝向自己
     */
    public static boolean isFaceToFaceConnected(Level level, BlockPos pos, BlockState state, Block expectedOppositeBlock) {
        Direction facing = state.getValue(BlockStateProperties.FACING);
        BlockPos targetPos = pos.relative(facing);
        BlockState targetState = level.getBlockState(targetPos);

        if (targetState.is(expectedOppositeBlock)) {
            Direction targetFacing = targetState.getValue(BlockStateProperties.FACING);
            return targetFacing == facing.getOpposite(); // 必须互相朝向对方
        }
        return false;
    }
}
