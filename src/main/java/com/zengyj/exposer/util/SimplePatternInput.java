package com.zengyj.exposer.util;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class SimplePatternInput implements IPatternDetails.IInput {

    private final GenericStack[] possibleInputs;
    private final long multiplier;

    public SimplePatternInput(GenericStack[] possibleInputs) {
        this(possibleInputs, 1L);
    }

    public SimplePatternInput(GenericStack[] possibleInputs, long multiplier) {
        this.possibleInputs = possibleInputs;
        this.multiplier = multiplier;
    }

    /**
     * 返回当前槽位的所有候选输入（0号位为主原料，其余为替代品）
     */
    @Override
    public GenericStack[] getPossibleInputs() {
        return this.possibleInputs;
    }

    /**
     * 输入倍率，通常为 1
     */
    @Override
    public long getMultiplier() {
        return this.multiplier;
    }

    /**
     * 检查传入的 AEKey 是否是当前槽位合法的输入材料
     */
    @Override
    public boolean isValid(AEKey input, Level level) {
        if (this.possibleInputs == null || input == null) {
            return false;
        }

        // 遍历可能的输入列表，只要匹配其中任意一个候选 Key 即有效
        for (GenericStack stack : this.possibleInputs) {
            if (stack != null && stack.what().equals(input)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取消耗 template 物品后的残留物 (如：水桶 -> 空桶)
     */
    @Override
    public @Nullable AEKey getRemainingKey(AEKey template) {
        // 残留物处理仅针对物品（ItemStack）
        if (template instanceof AEItemKey itemKey) {
            ItemStack stack = itemKey.toStack();

            // 检查 Forge / Vanilla 原生物品是否有合成残留物
            if (stack.hasCraftingRemainingItem()) {
                ItemStack remainingItem = stack.getCraftingRemainingItem();
                if (!remainingItem.isEmpty()) {
                    return AEItemKey.of(remainingItem);
                }
            }
        }

        // 处理机制样板或无残留物时返回 null
        return null;
    }
}
