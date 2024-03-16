package com.zengyj.exposer.block;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.block.BlockNode;
import com.raoulvdberge.refinedstorage.block.info.BlockInfoBuilder;
import com.zengyj.exposer.Constants;
import com.zengyj.exposer.Exposer;
import com.zengyj.exposer.node.NetworkNodeExposer;
import com.zengyj.exposer.tile.TileExposer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockExposer extends BlockNode {

    public BlockExposer() {
        super(BlockInfoBuilder.forMod(Exposer.INSTANCE, Constants.Mod_ID, NetworkNodeExposer.ID).material(Material.IRON).soundType(SoundType.METAL).hardness(1.0F).tileEntity(TileExposer::new).create());
        GameRegistry.registerTileEntity(TileExposer.class,this.getRegistryName());
        setCreativeTab(RS.INSTANCE.tab);
    }
}
