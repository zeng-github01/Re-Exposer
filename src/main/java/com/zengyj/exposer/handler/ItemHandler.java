package com.zengyj.exposer.handler;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.storage.IStorageCacheListener;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemHandler implements IItemHandler, IStorageCacheListener<ItemStack> {
    private INetwork network;
    private ItemStack[] storageCacheData;

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
        return slot < this.storageCacheData.length ? this.storageCacheData[slot] : ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return StackUtils.nullToEmpty(this.network.insertItem(stack, stack.getCount(), simulate ? Action.SIMULATE : Action.PERFORM));
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return slot < this.storageCacheData.length ? StackUtils.nullToEmpty(this.network.extractItem(this.storageCacheData[slot], amount, 3, simulate ? Action.SIMULATE : Action.PERFORM)) : ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    @Override
    public void onAttached() {

    }

    @Override
    public void onInvalidated() {
        this.invalidate();
    }

    @Override
    public void onChanged(@Nonnull ItemStack itemStack, int i) {
        this.invalidate();
    }

    @Override
    public void onChangedBulk(@Nonnull List<Pair<ItemStack, Integer>> list) {
        this.invalidate();
    }

    private void invalidate(){
        this.storageCacheData = this.network.getItemStorageCache().getList().getStacks().toArray(new ItemStack[0]);
    }
}
