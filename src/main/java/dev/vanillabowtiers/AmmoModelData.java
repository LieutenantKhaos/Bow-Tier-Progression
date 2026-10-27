package dev.vanillabowtiers;

import java.util.ArrayList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;

/** String slot zero is reserved by VBT's nocked-arrow model selectors. */
public final class AmmoModelData {
    private AmmoModelData() {}

    public static String tier(ItemStack ammo) {
        if (ammo.is(ModItems.COPPER_ARROW)) return "copper";
        if (ammo.is(ModItems.IRON_ARROW)) return "iron";
        if (ammo.is(ModItems.GOLDEN_ARROW)) return "golden";
        if (ammo.is(ModItems.DIAMOND_ARROW)) return "diamond";
        if (ammo.is(ModItems.NETHERITE_ARROW)) return "netherite";
        if (ammo.is(ModItems.ENDERITE_ARROW)) return "enderite";
        return "vanilla";
    }

    public static void set(ItemStack bow, String tier) {
        CustomModelData previous = bow.getOrDefault(DataComponents.CUSTOM_MODEL_DATA, CustomModelData.EMPTY);
        ArrayList<String> strings = new ArrayList<>(previous.strings());
        if (strings.isEmpty()) strings.add(tier);
        else strings.set(0, tier);
        CustomModelData updated = new CustomModelData(previous.floats(), previous.flags(), strings, previous.colors());
        if (!updated.equals(previous)) bow.set(DataComponents.CUSTOM_MODEL_DATA, updated);
    }
}