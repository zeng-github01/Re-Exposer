package com.zengyj.exposer.handler;


import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.network.Network;
import com.refinedmods.refinedstorage.api.network.storage.StorageNetworkComponent;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.list.MutableResourceList;
import com.refinedmods.refinedstorage.api.resource.list.listenable.ResourceListListener;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.api.storage.root.RootStorageListener;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage.common.support.resource.FluidResource;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.checkerframework.checker.nullness.qual.NonNull;


import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FluidHandler implements IFluidHandler, ResourceListListener {

    private final Network network;
    private List<ResourceAmount> cached;

    public FluidHandler(Network network) {
        this.network = network;
        invalidate();
    }

    @Override
    public int getTanks() {
        if (cached == null) return 0;

        return cached.size() + 1;
    }

    @NonNull
    @Override
    public FluidStack getFluidInTank(int i) {
        if (cached == null || i >= cached.size()) return FluidStack.EMPTY;

        ResourceAmount resourceAmount = cached.get(i);
        Fluid fluid = ((FluidResource) resourceAmount.resource()).fluid();
        if (resourceAmount.amount() <= Integer.MAX_VALUE) {
            return new FluidStack(fluid, ((int) resourceAmount.amount()));
        } else {
            return new FluidStack(fluid, Integer.MAX_VALUE);
        }
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
                return new FluidStack(fluidStack.getFluid(), ((int) extracted_execute));
            case SIMULATE:
                long extracted_simulate = component.extract(fluidResource, fluidStack.getAmount(), Action.SIMULATE, Actor.EMPTY);
                return new FluidStack(fluidStack.getFluid(), ((int) extracted_simulate));
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
        invalidate();
    }

    private void invalidate() {
        if (network == null) {
            cached = List.of();
            return;
        }
        StorageNetworkComponent storage = network.getComponent(StorageNetworkComponent.class);

        // 核心修复：按照 FluidResource 字符串 ID 确定性排序，保持 Tank 索引绝对固定
        cached = storage.getAll().stream()
                .filter(res -> res.resource() instanceof FluidResource)
                .sorted(Comparator.comparing(res -> res.resource().toString()))
                .toList();
    }
}
