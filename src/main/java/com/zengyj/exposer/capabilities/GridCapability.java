package com.zengyj.exposer.capabilities;

import com.refinedmods.refinedstorage.tile.ExternalStorageTile;
import com.refinedmods.refinedstorage.tile.grid.GridTile;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GridCapability implements ICapabilityProvider {

    private GridTile grid;
    private TileEntity core ;

    public GridCapability(GridTile grid) {
        this.grid = grid;
    }

    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable Direction direction) {
        if ((capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) && this.grid.getNode().getNetwork() instanceof TileEntity) {
                if (direction != null) {
                BlockPos pos = this.grid.getBlockPos().offset(direction.getNormal());
                if (this.grid.getLevel().isLoaded(pos)) {
                    TileEntity te = this.grid.getLevel().getBlockEntity(pos);
                    if (te instanceof ExternalStorageTile) {
                        return false;
                    }
                }
            }

            if (this.core == null || !(this.grid.getNode()).isActive()) {
                this.core = (TileEntity)(this.grid.getNode()).getNetwork();
            }

            return true;
        } else {
            return false;
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction direction) {
        return this.hasCapability(capability, direction) ? this.core.getCapability(capability, (Direction) null) : null;
    }
}
