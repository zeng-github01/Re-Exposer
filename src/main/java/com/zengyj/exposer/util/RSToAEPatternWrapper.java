package com.zengyj.exposer.util;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class RSToAEPatternWrapper implements IPatternDetails {
    private final ICraftingPattern rsPattern;

    public RSToAEPatternWrapper(ICraftingPattern rsPattern) {
        this.rsPattern = rsPattern;
    }

    public ICraftingPattern getRsPattern() {
        return rsPattern;
    }

    @Override
    public AEItemKey getDefinition() {
        // 返回代表此样板的定义（可返回输入产物生成的虚拟 Key）
        ItemStack output = rsPattern.getOutputs().get(0);
        return AEItemKey.of(output);
    }

    @Override
    public IInput[] getInputs() {
        // 将 RS 的 inputs 转换成 AE2 的 IPatternInput 数组
        return rsPattern.getInputs().stream()
                .map(list -> {
                    GenericStack[] stacks = list.stream()
                            .map(stack -> new GenericStack(AEItemKey.of(stack), stack.getCount()))
                            .toArray(GenericStack[]::new);
                    return new SimplePatternInput(stacks);
                }).toArray(IInput[]::new);
    }

    @Override
    public GenericStack[] getOutputs() {
        // 将 RS 的 outputs 转换成 AE2 的 GenericStack 数组
        return rsPattern.getOutputs().stream()
                .map(stack -> new GenericStack(AEItemKey.of(stack), stack.getCount()))
                .toArray(GenericStack[]::new);
    }
}
