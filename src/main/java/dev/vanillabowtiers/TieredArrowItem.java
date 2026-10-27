package dev.vanillabowtiers;

import dev.vanillabowtiers.access.EnderiteArrowAccess;
import dev.vanillabowtiers.access.ProjectileTierAccess;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public final class TieredArrowItem extends ArrowItem {
    private final double damageMultiplier;

    public TieredArrowItem(Properties properties, double damageMultiplier) {
        super(properties);
        this.damageMultiplier = damageMultiplier;
    }

    public double damageMultiplier() {
        return damageMultiplier;
    }

    @Override
    public AbstractArrow createArrow(
            Level level,
            ItemStack pickupStack,
            LivingEntity owner,
            @Nullable ItemStack firedFromWeapon
    ) {
        AbstractArrow arrow = super.createArrow(level, pickupStack, owner, firedFromWeapon);
        arrow.setBaseDamage(2.0D * damageMultiplier);
        if (arrow instanceof ProjectileTierAccess tierAccess) {
            int tier = this == ModItems.COPPER_ARROW ? 6
                    : this == ModItems.IRON_ARROW ? 1
                    : this == ModItems.GOLDEN_ARROW ? 2
                    : this == ModItems.DIAMOND_ARROW ? 3
                    : this == ModItems.NETHERITE_ARROW ? 4
                    : this == ModItems.ENDERITE_ARROW ? 5 : 0;
            tierAccess.vanillaBowTiers$setProjectileTier(tier);
        }

        if (this == ModItems.ENDERITE_ARROW && arrow instanceof EnderiteArrowAccess enderiteArrowAccess) {
            enderiteArrowAccess.vanillaBowTiers$markEnderiteArrow();
        }
        return arrow;
    }
}
