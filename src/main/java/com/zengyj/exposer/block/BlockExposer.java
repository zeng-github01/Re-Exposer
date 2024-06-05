package com.zengyj.exposer.block;

import com.refinedmods.refinedstorage.block.BlockDirection;
import com.refinedmods.refinedstorage.block.NetworkNodeBlock;
import com.refinedmods.refinedstorage.util.BlockUtils;
import com.zengyj.exposer.Exposer;
import com.zengyj.exposer.tile.TileExposer;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class BlockExposer extends NetworkNodeBlock {

    public BlockExposer() {
        super(BlockUtils.DEFAULT_ROCK_PROPERTIES);
        Exposer.LOGGER.info("TranslationKey:{}", getTranslationKey());
    }

    public BlockDirection getDirection() {
        return BlockDirection.HORIZONTAL;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileExposer();
    }

    @Override
    public String getTranslationKey() {
        return "block.exposer";
    }
}
