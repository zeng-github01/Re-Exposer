package com.zengyj.exposer.util;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RSToAEPatternWrapper implements IPatternDetails {
    private final ICraftingPattern rsPattern;
    private final IInput[] inputs;
    private final GenericStack[] outputs;
    private final AEItemKey definitionKey;

    public RSToAEPatternWrapper(ICraftingPattern rsPattern) {
        this.rsPattern = rsPattern;

        // 1. 安全转换输出 (Outputs)
        List<GenericStack> outputList = new ArrayList<>();
        for (ItemStack stack : rsPattern.getOutputs()) {
            if (stack != null && !stack.isEmpty()) {
                AEItemKey key = AEItemKey.of(stack);
                if (key != null) {
                    outputList.add(new GenericStack(key, stack.getCount()));
                }
            }
        }

        for (FluidStack stack : rsPattern.getFluidOutputs()) {
            if (stack != null && !stack.isEmpty()) {
                AEFluidKey key = AEFluidKey.of(stack);
                if (key != null) {
                    outputList.add(new GenericStack(key, stack.getAmount()));
                }
            }
        }
        this.outputs = outputList.toArray(new GenericStack[0]);

        // 2. 安全计算定义 Key (Definition Key)
        if (this.outputs.length > 0) {
            this.definitionKey = (AEItemKey) this.outputs[0].what();
        } else {
            this.definitionKey = null;
        }

        // 3. 安全转换输入 (Inputs)
        List<IInput> inputList = new ArrayList<>();
        if (rsPattern.getInputs() != null) {
            for (List<ItemStack> rsInputGroup : rsPattern.getInputs()) {
                if (rsInputGroup == null || rsInputGroup.isEmpty()) continue;

                List<GenericStack> optionStacks = new ArrayList<>();
                for (ItemStack stack : rsInputGroup) {
                    if (stack != null && !stack.isEmpty()) {
                        AEItemKey key = AEItemKey.of(stack);
                        if (key != null) {
                            optionStacks.add(new GenericStack(key, stack.getCount()));
                        }
                    }
                }

                if (!optionStacks.isEmpty()) {
                    inputList.add(new SimplePatternInput(optionStacks.toArray(new GenericStack[0])));
                }
            }

            for (List<FluidStack> rsInputGroup : rsPattern.getFluidInputs()) {
                if (rsInputGroup == null || rsInputGroup.isEmpty()) continue;

                List<GenericStack> optionStacks = new ArrayList<>();
                for (FluidStack stack : rsInputGroup) {
                    if (stack != null && !stack.isEmpty()) {
                        AEFluidKey key = AEFluidKey.of(stack);
                        if (key != null) {
                            optionStacks.add(new GenericStack(key, stack.getAmount()));
                        }
                    }
                }

                if (!optionStacks.isEmpty()) {
                    inputList.add(new SimplePatternInput(optionStacks.toArray(new GenericStack[0])));
                }
            }
        }
        this.inputs = inputList.toArray(new IInput[0]);
    }

    public ICraftingPattern getRsPattern() {
        return rsPattern;
    }

    /**
     * 💡 关键防御手段：确保传给 AE2 的样板至少有一个有效的输出和输入！
     */
    public boolean isValid() {
        return this.outputs.length > 0 && this.definitionKey != null;
    }

    @Override
    public AEItemKey getDefinition() {
        return this.definitionKey;
    }

    @Override
    public IInput[] getInputs() {
        return this.inputs;
    }

    @Override
    public GenericStack[] getOutputs() {
        return this.outputs;
    }

    // ==================== 必须重写 Equals 与 HashCode ====================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RSToAEPatternWrapper that = (RSToAEPatternWrapper) o;
        return Objects.equals(rsPattern, that.rsPattern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rsPattern);
    }
}
