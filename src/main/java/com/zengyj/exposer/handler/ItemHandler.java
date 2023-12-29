package com.zengyj.exposer.handler;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.storage.cache.IStorageCacheListener;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.api.util.StackListEntry;
import com.refinedmods.refinedstorage.api.util.StackListResult;
import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemHandler implements IItemHandler, IStorageCacheListener<ItemStack> {
    private INetwork network;
    private StackListEntry<ItemStack>[] storageCacheData;

    public ItemHandler(INetwork network){
        this.network = network;
        this.invalidate();
    }
    @Override
    public int getSlots() {
        return this.storageCacheData.length + 1;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return slot < this.storageCacheData.length ? this.storageCacheData[slot].getStack() : ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return this.network.insertItem(stack, stack.getCount(), simulate ? Action.SIMULATE : Action.PERFORM);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return slot < this.storageCacheData.length ? this.network.extractItem(this.storageCacheData[slot].getStack(), amount, 3, simulate ? Action.SIMULATE : Action.PERFORM) : ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    @Override
    public boolean isItemValid(int i, @Nonnull ItemStack itemStack) {
        return true;
    }

    @Override
    public void onAttached() {

    }

    @Override
    public void onInvalidated() {
        this.invalidate();
    }

    @Override
    public void onChanged(StackListResult<ItemStack> stackListResult) {
        this.invalidate();
    }

    @Override
    public void onChangedBulk(List<StackListResult<ItemStack>> list) {
        this.invalidate();
    }

    private void invalidate(){
        this.storageCacheData = this.network.getItemStorageCache().getList().getStacks().toArray(new StackListEntry[0]);
    }
}
