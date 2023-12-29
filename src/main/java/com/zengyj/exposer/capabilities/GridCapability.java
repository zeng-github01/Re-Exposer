package com.zengyj.exposer.capabilities;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeGrid;
import com.raoulvdberge.refinedstorage.tile.TileExternalStorage;
import com.raoulvdberge.refinedstorage.tile.grid.TileGrid;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GridCapability implements ICapabilityProvider {

    private TileGrid grid;
    private TileEntity core ;

    public GridCapability(TileGrid grid) {
        this.grid = grid;
    }

    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && ((NetworkNodeGrid)this.grid.getNode()).getNetwork() instanceof TileEntity) {
            if (facing != null) {
                BlockPos pos = this.grid.getPos().offset(facing);
                if (this.grid.getWorld().isBlockLoaded(pos)) {
                    TileEntity te = this.grid.getWorld().getTileEntity(pos);
                    if (te instanceof TileExternalStorage) {
                        return false;
                    }
                }
            }

            if (this.core == null || !((NetworkNodeGrid)this.grid.getNode()).isActive()) {
                this.core = (TileEntity)((NetworkNodeGrid)this.grid.getNode()).getNetwork();
            }

            return true;
        } else {
            return false;
        }
    }

    @Nullable
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return this.hasCapability(capability, facing) ? this.core.getCapability(capability, (EnumFacing)null) : null;
    }
}
