package com.zengyj.exposer.blockentity;

import appeng.api.config.Actionable;
import appeng.api.crafting.IPatternDetails;
import appeng.api.networking.IGrid;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.networking.crafting.ICraftingService;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import appeng.blockentity.ServerTickingBlockEntity;
import appeng.blockentity.grid.AENetworkBlockEntity;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.zengyj.exposer.Exposer;
import com.zengyj.exposer.Registry;
import com.zengyj.exposer.util.BlockLinkUtil;
import com.zengyj.exposer.util.RSToAEPatternWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class AEBridgeBlockEntity extends AENetworkBlockEntity implements ICraftingProvider, ServerTickingBlockEntity {
    public AEBridgeBlockEntity(BlockPos pos, BlockState blockState) {
        super(Registry.AE_BRIDGE_TYPE.get(), pos, blockState);
        this.getMainNode().addService(ICraftingProvider.class, this);
    }

    private int tickCounter = 0;
    private int lastRsPatternHash = -1;

    @Override
    public List<IPatternDetails> getAvailablePatterns() {
        List<IPatternDetails> patterns = new ArrayList<>();

        // 1. 检查是否与 RS 节点“对脸”连接
        if (!BlockLinkUtil.isFaceToFaceConnected(level, worldPosition, getBlockState(), Registry.RS_BRIDGE.get())) {
            return patterns;
        }

        // 2. 获取对面的 RS BlockEntity
        Direction facing = getBlockState().getValue(BlockStateProperties.FACING);
        BlockEntity targetBE = level.getBlockEntity(worldPosition.relative(facing));

        if (targetBE instanceof RSBridgeBlockEntity rsBE) {
            // 3. 将 RS 的 ICraftingPattern 转换成 AE2 的 IPatternDetails (包装类)
            for (ICraftingPattern rsPattern : rsBE.getRsPatterns()) {
                RSToAEPatternWrapper wrapper = new RSToAEPatternWrapper(rsPattern);

                // 💡 过滤掉无效/转换失败的样板，防止 AE2 的 CraftingTree 崩掉
                if (wrapper.isValid()) {
                    patterns.add(wrapper);
                }
            }
        }

        return patterns;
    }

    /**
     * 镜像查找：检查并获取对面“对脸”连接的 {@link RSBridgeBlockEntity}
     */
    @Nullable
    private RSBridgeBlockEntity getConnectedAEBridge() {
        if (this.level == null) return null;

        // 1. 检查是否与 AE 节点“对脸”连接
        if (!BlockLinkUtil.isFaceToFaceConnected(level, worldPosition, getBlockState(), Registry.AE_BRIDGE.get())) {
            return null;
        }

        // 2. 获取对面的 AE BlockEntity
        Direction facing = getBlockState().getValue(BlockStateProperties.FACING);
        BlockEntity targetBE = level.getBlockEntity(worldPosition.relative(facing));

        if (targetBE instanceof RSBridgeBlockEntity aeBE) {
            return aeBE;
        }

        return null;
    }

    @Override
    public boolean pushPattern(IPatternDetails patternDetails, KeyCounter[] inputHolder) {
        if (patternDetails == null) return false;

        // 1. 物理对脸连接校验
        if (!BlockLinkUtil.isFaceToFaceConnected(level, worldPosition, getBlockState(), Registry.RS_BRIDGE.get())) {
            return false;
        }

        Direction facing = getBlockState().getValue(BlockStateProperties.FACING);
        BlockEntity targetBE = level.getBlockEntity(worldPosition.relative(facing));

        if (!(targetBE instanceof RSBridgeBlockEntity rsBE)) {
            return false;
        }

        // 2. 获取并校验目标产物
        GenericStack primaryOutput = patternDetails.getPrimaryOutput();
        if (primaryOutput == null || primaryOutput.what() == null || primaryOutput.amount() <= 0) {
            return false;
        }

        // 💡【关键修复 1】：先尝试向 RS 发起合成请求！
        // 此时还没有向 RS 注入任何原材料。如果 RS 拒绝接单（无样板/断电/满载），直接返回 false！
        // AE2 收到 false 会安全保留物品，零刷物品风险。
        boolean requestSuccess = false;

        if (primaryOutput.what() instanceof AEItemKey itemKey) {
            ItemStack targetStack = itemKey.toStack((int) primaryOutput.amount());
            if (!targetStack.isEmpty()) {
                requestSuccess = rsBE.requestRSCrafting(targetStack, targetStack.getCount());
            }
        } else if (primaryOutput.what() instanceof AEFluidKey fluidKey) {
            FluidStack targetStack = fluidKey.toStack((int) primaryOutput.amount());
            if (!targetStack.isEmpty()) {
                requestSuccess = rsBE.requestRSCrafting(targetStack, targetStack.getAmount());
            }
        }

        // 如果 RS 无法发起合成，终止推送
        if (!requestSuccess) {
            return false;
        }

        // 💡【关键修复 2】：RS 确认接单后，再注入原材料，并记录已注入的物品列表
        List<ItemStack> injectedStacks = new ArrayList<>();
        boolean injectionFailed = false;

        for (KeyCounter counter : inputHolder) {
            for (var entry : counter) {
                if (entry.getKey() instanceof AEItemKey itemKey) {
                    ItemStack inputStack = itemKey.toStack((int) entry.getLongValue());
                    if (!inputStack.isEmpty()) {
                        // 尝试注入 RS
                        if (rsBE.injectItemToRS(inputStack)) {
                            injectedStacks.add(inputStack);
                        } else {
                            // 注入中途失败（如 RS 磁盘满了）
                            injectionFailed = true;
                            break;
                        }
                    }
                }
                // 如果后续要支持流体输入，也是同样的逻辑：
                // else if (entry.getKey() instanceof AEFluidKey fluidKey) { ... }
            }
            if (injectionFailed) break;
        }

        // 💡【关键修复 3】：若注入中途失败，紧急抽回已注入物品（回滚）
        if (injectionFailed) {
            for (ItemStack stack : injectedStacks) {
                // 假设你的 RS 侧有抽回物品的方法，把刚才投进去的再抽出来
                rsBE.extractItemFromRS(stack);
            }
            return false;
        }

        // 只有当 RS 成功接单，且原材料全部顺利注入 RS 时，才返回 true 给 AE2！
        // AE2 收到 true 会立刻在其网络/CPU 中扣除这些原材料，完成平滑平移。
        return true;
    }

    @Override
    public boolean isBusy() {
        return false;
    }

    @Override
    public void serverTick() {
        tickCounter++;
        // 每 10 个 Tick (0.5 秒) 检查一次 RS 样板是否更新
        if (tickCounter % 10 == 0) {
            checkAndRefreshPatterns();
        }
    }

    private void checkAndRefreshPatterns() {
        if (level == null || level.isClientSide) return;

        // 1. 获取对脸相连的 RS 方块
        Direction facing = getBlockState().getValue(BlockStateProperties.FACING);
        if (level.getBlockEntity(worldPosition.relative(facing)) instanceof RSBridgeBlockEntity rsBE) {

            // 2. 计算当前 RS 样板列表的组合 Hash
            List<ICraftingPattern> rsPatterns = (List<ICraftingPattern>) rsBE.getRsPatterns();
            int currentHash = calculatePatternsHash(rsPatterns);

            // 3. 如果 Hash 改变，说明 RS 样板发生了增/减/修改！
            if (currentHash != lastRsPatternHash) {
                lastRsPatternHash = currentHash;
                ICraftingProvider.requestUpdate(getMainNode());
                Exposer.LOGGER.info("Detected RS Pattern changes! AE2 Crafting Provider refreshed automatically.");
            }
        } else {
            // 如果连接断开，重置 hash
            if (lastRsPatternHash != -1) {
                lastRsPatternHash = -1;
                ICraftingProvider.requestUpdate(getMainNode());
            }
        }
    }

    private int calculatePatternsHash(List<ICraftingPattern> patterns) {
        if (patterns == null || patterns.isEmpty()) return 0;
        return patterns.hashCode();
    }

    /**
     * 将 GenericStack（物品、流体等通用产物）直接注入 AE2 网络
     */
    public void injectToAE2(GenericStack stack) {
        if (stack == null || stack.what() == null || stack.amount() <= 0) return;

        getMainNode().ifPresent((grid, node) -> {
            // 1. 将产物写入 AE2 网络
            long inserted = grid.getStorageService().getInventory().insert(
                    stack.what(),
                    stack.amount(),
                    Actionable.MODULATE,
                    IActionSource.ofMachine(this)
            );
        });
    }
}
