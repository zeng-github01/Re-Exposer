package com.zengyj.exposer.blockentity;

import appeng.api.crafting.IPatternDetails;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.stacks.KeyCounter;
import appeng.blockentity.grid.AENetworkBlockEntity;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.zengyj.exposer.Exposer;
import com.zengyj.exposer.Registry;
import com.zengyj.exposer.util.BlockLinkUtil;
import com.zengyj.exposer.util.RSToAEPatternWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.ArrayList;
import java.util.List;

public class AEBridgeBlockEntity extends AENetworkBlockEntity implements ICraftingProvider {
    public AEBridgeBlockEntity(BlockPos pos, BlockState blockState) {
        super(Registry.AE_BRIDGE_TYPE.get(), pos, blockState);
    }

    @Override
    public List<IPatternDetails> getAvailablePatterns() {
        List<IPatternDetails> patterns = new ArrayList<>();
        Exposer.LOGGER.info("GET PATTERN LIST");

        // 1. 检查是否与 RS 节点“对脸”连接
        if (!BlockLinkUtil.isFaceToFaceConnected(level, worldPosition, getBlockState(), Registry.RS_BRIDGE.get())) {
            Exposer.LOGGER.info("NOT FACE CONNECTED");
            return patterns;
        }

        // 2. 获取对面的 RS BlockEntity
        Direction facing = getBlockState().getValue(BlockStateProperties.FACING);
        BlockEntity targetBE = level.getBlockEntity(worldPosition.relative(facing));

        if (targetBE instanceof RSBridgeBlockEntity rsBE) {
            Exposer.LOGGER.info("GET BRIDGE");
            // 3. 将 RS 的 ICraftingPattern 转换成 AE2 的 IPatternDetails (包装类)
            for (ICraftingPattern rsPattern : rsBE.getRsPatterns()) {
                patterns.add(new RSToAEPatternWrapper(rsPattern));
            }
        }

        return patterns;
    }

    @Override
    public boolean pushPattern(IPatternDetails iPatternDetails, KeyCounter[] keyCounters) {
        return false;
    }

    @Override
    public boolean isBusy() {
        return false;
    }
}
