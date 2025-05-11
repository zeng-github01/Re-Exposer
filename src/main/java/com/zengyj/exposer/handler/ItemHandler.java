package com.zengyj.exposer.handler;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.network.Network;
import com.refinedmods.refinedstorage.api.network.storage.StorageNetworkComponent;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.list.MutableResourceList;
import com.refinedmods.refinedstorage.api.resource.list.listenable.ResourceListListener;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceFactory;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;
import com.zengyj.exposer.Exposer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

//TODO 实现错误

public class ItemHandler implements IItemHandler, ResourceListListener {
    private final Network network;
    private List<ResourceAmount> cached;

    public ItemHandler(Network network) {
        this.network = network;
        invalidate();
    }

    @Override
    public int getSlots() {
        if (cached == null) return 0;

        return cached.size() + 1;
    }

    @NonNull
    @Override
    public ItemStack getStackInSlot(int slot) {
        if (cached == null || slot >= this.cached.size()) return ItemStack.EMPTY;

        ResourceAmount resourceAmount = this.cached.get(slot);
        return ((ItemResource) resourceAmount.resource()).toItemStack(resourceAmount.amount());
    }

    @NonNull
    @Override
    public ItemStack insertItem(int slot, @NonNull ItemStack stack, boolean simulate) {
        ItemStack copied = stack.copy();
        ResourceFactory itemResourceFactory = RefinedStorageApi.INSTANCE.getItemResourceFactory();
        Optional<ResourceAmount> resourceAmount = itemResourceFactory.create(stack);
        if (resourceAmount.isPresent()) {
            long inserted = network.getComponent(StorageNetworkComponent.class).insert(resourceAmount.get().resource(), resourceAmount.get().amount(), simulate ? Action.SIMULATE : Action.EXECUTE, Actor.EMPTY);
            long rest = stack.getCount() - inserted;
            copied.setCount(((int) rest));

            if (rest <= 0) {
                return ItemStack.EMPTY;
            }
        }
        return copied;
    }

    @NonNull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount <= 0) return ItemStack.EMPTY;

        if (slot >= cached.size()) return ItemStack.EMPTY;
        ResourceAmount resourceAmount = cached.get(slot);

        long extracted = network.getComponent(StorageNetworkComponent.class).extract(resourceAmount.resource(), amount, simulate ? Action.SIMULATE : Action.EXECUTE, Actor.EMPTY);

        if (extracted <= 0) {
            return ItemStack.EMPTY;
        }

        return ((ItemResource) resourceAmount.resource()).toItemStack(extracted);
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    @Override
    public boolean isItemValid(int i, @NonNull ItemStack itemStack) {
        return true;
    }

    @Override
    public void changed(MutableResourceList.OperationResult operationResult) {
        invalidate();
    }

    private void invalidate() {
        if (network == null) {
            cached = List.of();
            return;
        }
        StorageNetworkComponent storage = network.getComponent(StorageNetworkComponent.class);

        // 4. 核心修复：按资源 ID/字符串做确定性排序（Deterministic Sorting）
        // 保证每次 invalidate 之后，已有物品的 Slot 索引绝对不会发生乱序或漂移！
        cached = storage.getAll().stream()
                .filter(res -> res.resource() instanceof ItemResource)
                .sorted(Comparator.comparing(res -> res.resource().toString()))
                .toList();
    }
}
