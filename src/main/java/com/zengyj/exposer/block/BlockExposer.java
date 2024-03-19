package com.zengyj.exposer.block;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.block.BlockNode;
import com.raoulvdberge.refinedstorage.block.info.BlockInfoBuilder;
import com.zengyj.exposer.Constants;
import com.zengyj.exposer.Exposer;
import com.zengyj.exposer.tile.TileExposer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockExposer extends BlockNode {

    public BlockExposer() {
        super(BlockInfoBuilder.forMod(Exposer.INSTANCE, Constants.Mod_ID,"exposer").soundType(SoundType.METAL).hardness(0.35F).tileEntity(TileExposer::new).material(Material.IRON).create());
        GameRegistry.registerTileEntity(TileExposer.class, getRegistryName());
        setCreativeTab(RS.INSTANCE.tab);
    }

    @Override
    public String getTranslationKey() {
        return "block.exposer";
    }


}
