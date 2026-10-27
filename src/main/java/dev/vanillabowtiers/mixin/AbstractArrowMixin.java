package dev.vanillabowtiers.mixin;

import dev.vanillabowtiers.ModItems;
import dev.vanillabowtiers.access.EnderiteArrowAccess;
import dev.vanillabowtiers.access.SharpshooterArrowAccess;
import dev.vanillabowtiers.access.ProjectileTierAccess;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin implements SharpshooterArrowAccess, EnderiteArrowAccess, ProjectileTierAccess {
    @Unique private static final EntityDataAccessor<Byte> VANILLA_BOW_TIERS$SHARPSHOOTER_LEVEL =
            SynchedEntityData.defineId(AbstractArrow.class, EntityDataSerializers.BYTE);
    @Unique private static final EntityDataAccessor<Byte> VANILLA_BOW_TIERS$PROJECTILE_TIER =
            SynchedEntityData.defineId(AbstractArrow.class, EntityDataSerializers.BYTE);
    @Unique private boolean vanillaBowTiers$enderiteArrow = false;
    @Unique private boolean vanillaBowTiers$checkedWeakEnderiteShot = false;
    @Unique private int vanillaBowTiers$weakEnderiteShotTicks = -1;

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void vanillaBowTiers$defineSharpshooterData(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(VANILLA_BOW_TIERS$SHARPSHOOTER_LEVEL, (byte) 0);
        builder.define(VANILLA_BOW_TIERS$PROJECTILE_TIER, (byte) 0);
    }

    @Override public void vanillaBowTiers$setSharpshooterLevel(int level) {
        ((AbstractArrow)(Object)this).getEntityData().set(VANILLA_BOW_TIERS$SHARPSHOOTER_LEVEL,
                (byte)Math.max(0, Math.min(4, level)));
    }
    @Override public int vanillaBowTiers$getSharpshooterLevel() {
        return ((AbstractArrow)(Object)this).getEntityData().get(VANILLA_BOW_TIERS$SHARPSHOOTER_LEVEL);
    }
    @Override public void vanillaBowTiers$setProjectileTier(int tier) {
        ((AbstractArrow)(Object)this).getEntityData().set(VANILLA_BOW_TIERS$PROJECTILE_TIER,
                (byte)Math.max(0, Math.min(6, tier)));
    }
    @Override public int vanillaBowTiers$getProjectileTier() {
        return ((AbstractArrow)(Object)this).getEntityData().get(VANILLA_BOW_TIERS$PROJECTILE_TIER);
    }
    @Override public void vanillaBowTiers$markEnderiteArrow() { vanillaBowTiers$enderiteArrow = true; }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void vanillaBowTiers$saveState(ValueOutput output, CallbackInfo ci) {
        output.putInt("vbt_projectile_tier", vanillaBowTiers$getProjectileTier());
        output.putInt("vbt_sharpshooter", vanillaBowTiers$getSharpshooterLevel());
        output.putBoolean("vbt_enderite", vanillaBowTiers$enderiteArrow);
        output.putBoolean("vbt_weak_checked", vanillaBowTiers$checkedWeakEnderiteShot);
        output.putInt("vbt_weak_ticks", vanillaBowTiers$weakEnderiteShotTicks);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void vanillaBowTiers$loadState(ValueInput input, CallbackInfo ci) {
        AbstractArrow arrow = (AbstractArrow)(Object)this;
        ItemStack pickup = arrow.getPickupItemStackOrigin();
        int fallbackTier = pickup.is(ModItems.COPPER_ARROW) ? 6
                : pickup.is(ModItems.IRON_ARROW) ? 1 : pickup.is(ModItems.GOLDEN_ARROW) ? 2
                : pickup.is(ModItems.DIAMOND_ARROW) ? 3 : pickup.is(ModItems.NETHERITE_ARROW) ? 4
                : pickup.is(ModItems.ENDERITE_ARROW) ? 5 : 0;
        vanillaBowTiers$setProjectileTier(input.getIntOr("vbt_projectile_tier", fallbackTier));
        vanillaBowTiers$setSharpshooterLevel(input.getIntOr("vbt_sharpshooter", 0));
        vanillaBowTiers$enderiteArrow = input.getBooleanOr("vbt_enderite", fallbackTier == 5);
        vanillaBowTiers$checkedWeakEnderiteShot = input.getBooleanOr("vbt_weak_checked", true);
        vanillaBowTiers$weakEnderiteShotTicks = input.getIntOr("vbt_weak_ticks", -1);
    }
    @Inject(method = "tick", at = @At("TAIL"))
    private void vanillaBowTiers$recoverWeakEnderiteArrow(CallbackInfo ci) {
        AbstractArrow arrow = (AbstractArrow)(Object)this;
        if (!vanillaBowTiers$enderiteArrow || arrow.level().isClientSide() || arrow.isRemoved()) return;
        if (!vanillaBowTiers$checkedWeakEnderiteShot) {
            vanillaBowTiers$checkedWeakEnderiteShot = true;
            if (arrow.getDeltaMovement().lengthSqr() < 0.25D) vanillaBowTiers$weakEnderiteShotTicks = 60;
        }
        if (vanillaBowTiers$weakEnderiteShotTicks > 0) vanillaBowTiers$weakEnderiteShotTicks--;
        if (vanillaBowTiers$weakEnderiteShotTicks == 0) {
            if (arrow.pickup == AbstractArrow.Pickup.ALLOWED) {
            ItemEntity droppedArrow = new ItemEntity(arrow.level(), arrow.getX(), arrow.getY(), arrow.getZ(),
                    arrow.getPickupItemStackOrigin().copyWithCount(1));
            droppedArrow.setDefaultPickUpDelay();
            droppedArrow.setDeltaMovement(arrow.getDeltaMovement().scale(0.25D));
            arrow.level().addFreshEntity(droppedArrow);
            }
            arrow.discard();
            vanillaBowTiers$weakEnderiteShotTicks = -1;
        }
    }

    @Inject(method = "getDefaultGravity", at = @At("RETURN"), cancellable = true)
    private void vanillaBowTiers$sharpshooterGravity(CallbackInfoReturnable<Double> cir) {
        int level = vanillaBowTiers$getSharpshooterLevel();
        double factor = switch (level) {
            case 1 -> 0.75D; case 2 -> 0.50D; case 3 -> 0.25D; case 4 -> 0.10D; default -> 1.0D;
        };
        if (factor != 1.0D) cir.setReturnValue(cir.getReturnValue() * factor);
    }
}
