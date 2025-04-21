package com.zengyj.exposer.handler;


import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.network.Network;
import com.refinedmods.refinedstorage.api.network.storage.StorageNetworkComponent;
import com.refinedmods.refinedstorage.api.resource.list.MutableResourceList;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.api.storage.root.RootStorageListener;
import com.refinedmods.refinedstorage.common.support.resource.FluidResource;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.checkerframework.checker.nullness.qual.NonNull;


import java.util.List;

public class FluidHandler implements IFluidHandler, RootStorageListener {

    private Network network;

    public FluidHandler(Network network) {
        this.network = network;
    }

    @Override
    public int getTanks() {
//        return storageCacheData.length + 1;
    }

    @NonNull
    @Override
    public FluidStack getFluidInTank(int i) {
//        return i < this.storageCacheData.length ? this.storageCacheData[i].getStack() : FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(int i) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isFluidValid(int i, @NonNull FluidStack fluidStack) {
        return true;
    }

    @Override
    public int fill(FluidStack fluidStack, FluidAction fluidAction) {
        if (fluidStack == FluidStack.EMPTY) return 0;

        StorageNetworkComponent component = network.getComponent(StorageNetworkComponent.class);

        FluidResource fluidResource = new FluidResource(fluidStack.getFluid());

        long inserted = component.insert(fluidResource, fluidStack.getAmount(), fluidAction == FluidAction.EXECUTE ? Action.EXECUTE : Action.SIMULATE, Actor.EMPTY);

        return ((int) inserted);
    }

    @NonNull
    @Override
    public FluidStack drain(FluidStack fluidStack, FluidAction fluidAction) {
        StorageNetworkComponent component = network.getComponent(StorageNetworkComponent.class);
        FluidResource fluidResource = new FluidResource(fluidStack.getFluid());

        switch (fluidAction) {
            case EXECUTE:
                long extracted_execute = component.extract(fluidResource, fluidStack.getAmount(), Action.EXECUTE, Actor.EMPTY);
                return new FluidStack(fluidStack.getFluid(),((int) extracted_execute));
            case SIMULATE:
                long extracted_simulate = component.extract(fluidResource, fluidStack.getAmount(), Action.SIMULATE, Actor.EMPTY);
                return new FluidStack(fluidStack.getFluid(),((int) extracted_simulate));
        }
        return FluidStack.EMPTY;
    }

    @NonNull
    @Override
    public FluidStack drain(int i, FluidAction fluidAction) {
        return FluidStack.EMPTY;
    }

    @Override
    public void changed(MutableResourceList.OperationResult operationResult) {
        //
    }
}
