package com.zengyj.exposer.node;

import com.refinedmods.refinedstorage.api.network.impl.node.SimpleNetworkNode;
import com.refinedmods.refinedstorage.api.network.storage.StorageNetworkComponent;
import com.zengyj.exposer.handler.FluidHandler;
import com.zengyj.exposer.handler.ItemHandler;
import net.neoforged.neoforge.items.IItemHandler;

public class ExposerNetworkNode extends SimpleNetworkNode {
    public ItemHandler itemHandler;
    public FluidHandler fluidHandler;
    public ExposerNetworkNode(long energyUsage) {
        super(energyUsage);
    }

    @Override
    protected void onActiveChanged(boolean newActive) {
        if (network == null) return;
        if (newActive) {
            itemHandler = new ItemHandler(network);
            fluidHandler = new FluidHandler(network);
            network.getComponent(StorageNetworkComponent.class).addListener(itemHandler::changed);
            network.getComponent(StorageNetworkComponent.class).addListener(fluidHandler::changed);
        }else  {
            network.getComponent(StorageNetworkComponent.class).removeListener(itemHandler::changed);
            network.getComponent(StorageNetworkComponent.class).removeListener(fluidHandler::changed);
        }
    }
}
