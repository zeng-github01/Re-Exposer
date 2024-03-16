package com.zengyj.exposer.handler;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.storage.cache.IStorageCacheListener;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.api.util.StackListEntry;
import com.refinedmods.refinedstorage.api.util.StackListResult;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.List;

public class FluidHandler implements IFluidHandler, IStorageCacheListener<FluidStack> {

    private INetwork network;
    private StackListEntry<FluidStack>[] storageCacheData;

    public FluidHandler(INetwork network) {
        this.network = network;
        this.invalidate();
    }

    @Override
    public int getTanks() {
        return storageCacheData.length + 1;
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int i) {
        return i < this.storageCacheData.length ? this.storageCacheData[i].getStack() : FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(int i) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isFluidValid(int i, @Nonnull FluidStack fluidStack) {
        return true;
    }

    @Override
    public int fill(FluidStack fluidStack, FluidAction fluidAction) {
        if (fluidStack == FluidStack.EMPTY) return 0;

        FluidStack remainder = network.insertFluid(fluidStack, fluidStack.getAmount(), fluidAction == FluidAction.EXECUTE ? Action.PERFORM : Action.SIMULATE);
        if (remainder == FluidStack.EMPTY) {
            return fluidStack.getAmount();
        } else {
            return fluidStack.getAmount() - remainder.getAmount();
        }
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack fluidStack, FluidAction fluidAction) {
        switch (fluidAction) {
            case EXECUTE:
                return network.extractFluid(fluidStack, fluidStack.getAmount(), Action.PERFORM);
            case SIMULATE:
                return network.extractFluid(fluidStack, fluidStack.getAmount(), Action.SIMULATE);
        }
        return FluidStack.EMPTY;
    }

    @Nonnull
    @Override
    public FluidStack drain(int i, FluidAction fluidAction) {
        for (StackListEntry<FluidStack> fluidEntry : storageCacheData) {
            if (fluidEntry.getStack().getAmount() >= i) {
                return network.extractFluid(fluidEntry.getStack(), i, fluidAction == FluidAction.EXECUTE ? Action.PERFORM : Action.SIMULATE);
            }
        }
        return FluidStack.EMPTY;
    }

    @Override
    public void onAttached() {

    }

    @Override
    public void onInvalidated() {
        this.invalidate();
    }

    @Override
    public void onChanged(StackListResult<FluidStack> stackListResult) {
        this.invalidate();
    }

    @Override
    public void onChangedBulk(List<StackListResult<FluidStack>> list) {
        this.invalidate();
    }

    private void invalidate() {
        this.storageCacheData = this.network.getFluidStorageCache().getList().getStacks().toArray(new StackListEntry[0]);
    }
}
