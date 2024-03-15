package com.zengyj.exposer.handler;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.storage.IStorage;
import com.raoulvdberge.refinedstorage.api.storage.IStorageCacheListener;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDisk;
import com.raoulvdberge.refinedstorage.api.storage.externalstorage.IStorageExternal;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.zengyj.exposer.Exposer;
import com.zengyj.exposer.util.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class FluidHandler implements IFluidHandler, IStorageCacheListener<FluidStack> {

    private INetwork network;
    private FluidStack[] storageCacheData;

    public FluidHandler(INetwork network){
        this.network = network;
        this.invalidate();
    }
    @Override
    public IFluidTankProperties[] getTankProperties() {
        FluidTankProperties[] properties = new FluidTankProperties[storageCacheData.length+1];

        for (int i = 0; i < storageCacheData.length; i++) {
            properties[i] = new FluidTankProperties(storageCacheData[i].copy(),getCapacity());
        }

        FluidTankProperties tankProperties = new FluidTankProperties(null,getCapacity());
        properties[storageCacheData.length] = tankProperties;

        return properties;
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
        for (FluidStack fluidStack : storageCacheData) {
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
        this.invalidate();
    }

    @Override
    public void onChanged(@Nonnull FluidStack fluidStack, int i) {
        this.invalidate();
    }

    @Override
    public void onChangedBulk(@Nonnull List<Pair<FluidStack, Integer>> list) {
        this.invalidate();
    }

    private void invalidate(){
        this.storageCacheData = this.network.getFluidStorageCache().getList().getStacks().toArray(new FluidStack[0]);
    }

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
