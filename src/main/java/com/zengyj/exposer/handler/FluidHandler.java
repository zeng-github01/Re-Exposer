package com.zengyj.exposer.handler;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.storage.IStorage;
import com.raoulvdberge.refinedstorage.api.storage.IStorageCacheListener;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDisk;
import com.raoulvdberge.refinedstorage.api.storage.externalstorage.IStorageExternal;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class FluidHandler implements IFluidHandler, IStorageCacheListener<FluidStack> {

    private INetwork network;
//    private FluidStack[] storageCacheData;

    public FluidHandler(INetwork network){
        this.network = network;
//        this.invalidate();
    }
    @Override
    public IFluidTankProperties[] getTankProperties() {
        List<FluidTankProperties> properties = new ArrayList<>();
        for (FluidStack fluidStack : network.getFluidStorageCache().getList().getStacks()) {
            properties.add(new FluidTankProperties(fluidStack,getCapacity()));
        }

        return properties.toArray(new IFluidTankProperties[properties.size()]);
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        FluidStack fluidStack = network.insertFluid(resource, resource.amount, doFill ? Action.PERFORM : Action.SIMULATE);
        if (fluidStack == null) return 0;
        return fluidStack.amount;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        return network.extractFluid(resource, resource.amount, doDrain ? Action.PERFORM : Action.SIMULATE);
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        for (FluidStack fluidStack : network.getFluidStorageCache().getList().getStacks()) {
            if (maxDrain <= fluidStack.amount){
               return network.extractFluid(fluidStack,maxDrain,doDrain ? Action.PERFORM : Action.SIMULATE);
            }
        }

        return null;
    }

    @Override
    public void onAttached() {

    }

    @Override
    public void onInvalidated() {
//        this.invalidate();
    }

    @Override
    public void onChanged(@Nonnull FluidStack fluidStack, int i) {
//        this.invalidate();
    }

    @Override
    public void onChangedBulk(@Nonnull List<Pair<FluidStack, Integer>> list) {
//        this.invalidate();
    }

//    private void invalidate(){
//        this.storageCacheData = this.network.getFluidStorageCache().getList().getStacks().toArray(new FluidStack[0]);
//    }

    private int getCapacity(){
        int capacity = 0;
        if (network == null) return 0;

        for (IStorage<FluidStack> storage : this.network.getFluidStorageCache().getStorages()) {
            if(storage instanceof IStorageDisk){
                capacity += ((IStorageDisk<FluidStack>) storage).getCapacity();
            }

            if (storage instanceof IStorageExternal){
                capacity += ((IStorageExternal<FluidStack>) storage).getCapacity();
            }
        }

        return capacity;
    }
}
