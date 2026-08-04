package com.zengyj.exposer.blockentity;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorListener;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingRequestInfo;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import com.zengyj.exposer.Registry;
import com.zengyj.exposer.node.NetworkNodeBridge;
import com.zengyj.exposer.util.BlockLinkUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.*;

public class RSBridgeBlockEntity extends NetworkNodeBlockEntity<NetworkNodeBridge> {

    private static class TrackedRSTask {
        final ICraftingTask task;
        final GenericStack output;

        TrackedRSTask(ICraftingTask task, GenericStack output) {
            this.task = task;
            this.output = output;
        }
    }

    private final ICraftingMonitorListener monitorListener = new ICraftingMonitorListener() {
        @Override
        public void onAttached() {
            // 当服务器重启、区块重载或连入 RS 网络时自动触发
            // 重新扫描 RS 正在运行的任务，恢复内存中的 tracking 列表！
            rebuildTrackingListFromRS();
        }

        @Override
        public void onChanged() {
            // 合成任务状态变动（如任务完成）时触发结算
            checkAndSettleCompletedTasks();
        }
    };

    private final List<TrackedRSTask> trackedRSTasks = new ArrayList<>();
    private boolean isListenerRegistered = false;

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

    public boolean requestRSCrafting(ItemStack stack, int amount) {
        INetwork network = getNode().getNetwork();
        if (network == null || !network.canRun() || stack == null || stack.isEmpty() || amount <= 0) {
            return false;
        }

        registerRSListener();

        // 直接调用 RS 的 ItemStack 重载
        ICraftingTask task = network.getCraftingManager().request(this, stack, amount);

        // 记录 task 并转成 AE2 GenericStack 追踪
        return trackTask(task, new GenericStack(AEItemKey.of(stack), amount));
    }

    /**
     * 向 RS 网络请求合成指定流体 (FluidStack)
     */
    public boolean requestRSCrafting(FluidStack stack, int amount) {
        INetwork network = getNode().getNetwork();
        if (network == null || !network.canRun() || stack == null || stack.isEmpty() || amount <= 0) {
            return false;
        }

        registerRSListener();

        // 直接调用 RS 的 FluidStack 重载
        ICraftingTask task = network.getCraftingManager().request(this, stack, amount);

        // 记录 task 并转成 AE2 GenericStack 追踪
        return trackTask(task, new GenericStack(AEFluidKey.of(stack), amount));
    }

    /**
     * 唯一的公共提取函数：负责安全压入 trackedRSTasks 列表
     */
    private boolean trackTask(@Nullable ICraftingTask task, GenericStack aeOutput) {
        if (task != null) {
            synchronized (trackedRSTasks) {
                trackedRSTasks.add(new TrackedRSTask(task, aeOutput));
            }
            return true;
        }
        return false;
    }


    /**
     * 接收外部（如 AE2）传入的材料并直接注入 RS 网络
     *
     * @param stack 尝试输入的物品堆
     * @return true 表示成功全部存入 RS；false 表示 RS 空间不足或网络离线
     */
    public boolean injectItemToRS(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return true; // 空物品直接视作成功
        }

        INetwork network = getNode().getNetwork();
        if (network == null || !network.canRun()) {
            return false; // RS 网络无效/离线
        }

        // 1. 模拟插入（SIMULATE）：检查 RS 是否能全额装下
        ItemStack remainder = network.insertItem(stack, stack.getCount(), Action.SIMULATE);

        // 如果模拟结果有剩余，说明 RS 空间不足，拒绝接收
        if (!remainder.isEmpty()) {
            return false;
        }

        // 2. 正式插入（PERFORM）
        ItemStack realRemainder = network.insertItem(stack, stack.getCount(), Action.PERFORM);

        // 如果正式插入没有剩余，则返回成功
        return realRemainder.isEmpty();
    }

    /**
     * 从 RS 网络中抽取指定物品（用于 pushPattern 回滚或跨界提取）
     *
     * @param stack 要抽取的物品（包含 Item 和所需的 Count）
     * @return 实际成功从 RS 网络中抽出的 ItemStack（失败返回 ItemStack.EMPTY）
     */
    public ItemStack extractItemFromRS(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return extractItemFromRS(stack, stack.getCount());
    }

    /**
     * 从 RS 网络抽取指定数量的物品
     */
    public ItemStack extractItemFromRS(ItemStack stack, int amount) {
        if (stack == null || stack.isEmpty() || amount <= 0) {
            return ItemStack.EMPTY;
        }

        INetwork network = getNode().getNetwork();
        if (network != null && network.canRun()) {
            // 参数说明：
            // 1. stack: 目标的物品类型
            // 2. amount: 想要抽取的数量
            // 3. IComparer.COMPARE_NBT: 严格比对 NBT（部分 RS 版本 extractItem 包含此重载，若你的 API 无此参数可直接传 Action）
            // 4. Action.PERFORM: 实际执行抽取（非 SIMULATE 模拟）

            // 注：若你使用的 RS 版本 API 没有 flags 参数，直接走 (stack, amount, Action.PERFORM) 即可
            return network.extractItem(stack, amount, IComparer.COMPARE_NBT, Action.PERFORM);
        }

        return ItemStack.EMPTY;
    }


    // -----------------------------------------------------------------
    // 2. 重启自修复：从 RS 还原追踪列表
    // -----------------------------------------------------------------
    private void rebuildTrackingListFromRS() {
        INetwork rsNetwork = getNode().getNetwork();
        if (rsNetwork == null) return;

        synchronized (trackedRSTasks) {
            trackedRSTasks.clear();
            // 遍历 RS 网络中所有继承下来的未完成任务
            for (ICraftingTask task : rsNetwork.getCraftingManager().getTasks()) {
                GenericStack output = parseRSTaskOutput(task);
                if (output != null) {
                    trackedRSTasks.add(new TrackedRSTask(task, output));
                }
            }
        }
    }

    /**
     * 从 RS 任务对象中解析出 AE2 的 GenericStack 目标产物
     */
    private GenericStack parseRSTaskOutput(ICraftingTask task) {
        if (task == null) return null;

        ICraftingRequestInfo info = task.getRequested();
        if (info == null) return null;

        // 1. 优先检查是否为物品请求
        ItemStack item = info.getItem();
        if (item != null && !item.isEmpty()) {
            return new GenericStack(AEItemKey.of(item), item.getCount());
        }

        // 2. 检查是否为流体请求
        FluidStack fluid = info.getFluid();
        if (fluid != null && !fluid.isEmpty()) {
            return new GenericStack(AEFluidKey.of(fluid), fluid.getAmount());
        }

        return null;
    }

    private void checkAndSettleCompletedTasks() {
        INetwork rsNetwork = getNode().getNetwork();
        if (rsNetwork == null) return;

        var activeTasks = rsNetwork.getCraftingManager().getTasks();

        synchronized (trackedRSTasks) {
            Iterator<TrackedRSTask> iterator = trackedRSTasks.iterator();
            while (iterator.hasNext()) {
                TrackedRSTask tracked = iterator.next();

                // 若活跃任务列表中不再包含该 Task，说明 RS 已合成完毕！
                if (!activeTasks.contains(tracked.task)) {
                    // 从 RS 网络中提取出产物并注入 AE2
                    extractFromRSAndInjectToAE2(rsNetwork, tracked.output);
                    iterator.remove();
                }
            }
        }
    }

    /**
     * 镜像查找：检查并获取对面“对脸”连接的 AEBridgeBlockEntity
     */
    @Nullable
    private AEBridgeBlockEntity getConnectedAEBridge() {
        if (this.level == null) return null;

        // 1. 检查是否与 AE 节点“对脸”连接
        if (!BlockLinkUtil.isFaceToFaceConnected(level, worldPosition, getBlockState(), Registry.AE_BRIDGE.get())) {
            return null;
        }

        // 2. 获取对面的 AE BlockEntity
        Direction facing = getBlockState().getValue(BlockStateProperties.FACING);
        BlockEntity targetBE = level.getBlockEntity(worldPosition.relative(facing));

        if (targetBE instanceof AEBridgeBlockEntity aeBE) {
            return aeBE;
        }

        return null;
    }

    /**
     * 从 RS 抽出产物，并跨方块调用 AE 侧的 injectToAE2
     */
    private void extractFromRSAndInjectToAE2(INetwork rsNetwork, GenericStack output) {
        // 先确认对面 AE 桥接方块在线且已连接
        AEBridgeBlockEntity aeBE = getConnectedAEBridge();
        if (aeBE == null) {
            // 如果 AE2 桥接方块被拆除或断开，直接终止，防止物品被凭空抽取
            return;
        }

        // 1. 处理物品产物
        if (output.what() instanceof AEItemKey itemKey) {
            ItemStack query = itemKey.toStack((int) output.amount());
            ItemStack extracted = rsNetwork.extractItem(query, query.getCount(), Action.PERFORM);

            if (!extracted.isEmpty()) {
                // 💡 关键点：跨方块 Call AE 的注入函数
                aeBE.injectToAE2(output);
            }
        }
        // 2. 处理流体产物
        else if (output.what() instanceof AEFluidKey fluidKey) {
            FluidStack query = fluidKey.toStack((int) output.amount());
            FluidStack extracted = rsNetwork.extractFluid(query, query.getAmount(), Action.PERFORM);

            if (!extracted.isEmpty()) {
                // 💡 关键点：跨方块 Call AE 的注入函数
                aeBE.injectToAE2(new GenericStack(output.what(), extracted.getAmount()));
            }
        }
    }

    public void registerRSListener() {
        INetwork network = getNode().getNetwork();
        // 只有当 RS 网络可用，且尚未注册时才注册
        if (network != null && !isListenerRegistered) {
            network.getCraftingManager().addListener(this.monitorListener);
            this.isListenerRegistered = true;
        }
    }

    /**
     * 从 RS 网络中注销监听器（非常重要：防止方块拆除或断网后内存泄漏）
     */
    public void unregisterRSListener() {
        INetwork network = getNode().getNetwork();
        if (network != null && isListenerRegistered) {
            network.getCraftingManager().removeListener(this.monitorListener);
            this.isListenerRegistered = false;
        }
    }
}
