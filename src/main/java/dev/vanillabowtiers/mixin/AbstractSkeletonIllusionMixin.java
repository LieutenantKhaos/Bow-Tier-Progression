package dev.vanillabowtiers.mixin;

import dev.vanillabowtiers.ModItems;
import dev.vanillabowtiers.AmmoModelData;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.component.CustomModelData;
import dev.vanillabowtiers.VanillaBowTiers;
import dev.vanillabowtiers.TieredArrowItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.entity.monster.skeleton.WitherSkeleton;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.DifficultyInstance;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSkeleton.class)
public abstract class AbstractSkeletonIllusionMixin {
    private static final Identifier COPPER_MODEL = Identifier.fromNamespaceAndPath(VanillaBowTiers.MOD_ID, "copper_skeleton_bow");
    private static final Identifier IRON_MODEL = Identifier.fromNamespaceAndPath(VanillaBowTiers.MOD_ID, "iron_skeleton_bow");
    private static final Identifier GOLD_MODEL = Identifier.fromNamespaceAndPath(VanillaBowTiers.MOD_ID, "golden_skeleton_bow");
    private static final Identifier DIAMOND_MODEL = Identifier.fromNamespaceAndPath(VanillaBowTiers.MOD_ID, "diamond_skeleton_bow");
    private static final Identifier NETHERITE_MODEL = Identifier.fromNamespaceAndPath(VanillaBowTiers.MOD_ID, "netherite_skeleton_bow");

    @Inject(method = "finalizeSpawn", at = @At("RETURN"))
    private void vanillaBowTiers$assignIllusionTier(ServerLevelAccessor level, DifficultyInstance difficulty,
            EntitySpawnReason spawnReason, @Nullable SpawnGroupData spawnData, CallbackInfoReturnable<SpawnGroupData> cir) {
        AbstractSkeleton skeleton = (AbstractSkeleton)(Object)this;
        float roll = skeleton.getRandom().nextFloat();
        Identifier model = null;
        if (skeleton instanceof WitherSkeleton) {
            if (roll < 0.01F) model = NETHERITE_MODEL;
            else if (roll < 0.06F) model = DIAMOND_MODEL;
            else if (roll < 0.16F) model = GOLD_MODEL;
        } else if (skeleton.getClass() == Skeleton.class) {
            if (roll < 0.03F) model = DIAMOND_MODEL;
            else if (roll < 0.10F) model = GOLD_MODEL;
            else if (roll < 0.25F) model = IRON_MODEL;
            else if (roll < 0.45F) model = COPPER_MODEL;
        }
        if (model != null) {
            ItemStack bow = new ItemStack(Items.BOW);
            bow.set(DataComponents.ITEM_MODEL, model);
            skeleton.setItemSlot(EquipmentSlot.MAINHAND, bow);
            skeleton.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
            skeleton.reassessWeaponGoal();

        }
        ItemStack held = skeleton.getMainHandItem();
        if (held.is(Items.BOW)) vanillaBowTiers$assignAmmo(skeleton, held);
    }

    private static void vanillaBowTiers$assignAmmo(AbstractSkeleton skeleton, ItemStack bow) {
        CustomModelData marker = bow.get(DataComponents.CUSTOM_MODEL_DATA);
        if (marker != null && marker.getString(0) != null) return;
        Identifier model = bow.get(DataComponents.ITEM_MODEL);
        float roll = skeleton.getRandom().nextFloat();
        String ammo;
        if (COPPER_MODEL.equals(model)) ammo = "copper";
        else if (IRON_MODEL.equals(model)) ammo = roll < 0.40F ? "vanilla" : "iron";
        else if (GOLD_MODEL.equals(model)) ammo = roll < 0.20F ? "vanilla" : roll < 0.60F ? "iron" : "golden";
        else if (DIAMOND_MODEL.equals(model)) ammo = roll < 0.40F ? "iron" : roll < 0.70F ? "golden" : "diamond";
        else if (NETHERITE_MODEL.equals(model)) ammo = roll < 0.35F ? "iron" : roll < 0.65F ? "golden"
                : roll < 0.90F ? "diamond" : "netherite";
        else if (skeleton.getClass() == Skeleton.class && (model == null || Identifier.fromNamespaceAndPath("minecraft", "bow").equals(model))) ammo = roll < 0.80F ? "vanilla" : "iron";
        else return;
        AmmoModelData.set(bow, ammo);
    }
    @Inject(method = "canUseNonMeleeWeapon", at = @At("HEAD"), cancellable = true)
    private void vanillaBowTiers$acceptDisguisedVanillaBow(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.is(Items.BOW)) {
            Identifier model = stack.get(DataComponents.ITEM_MODEL);
            if (COPPER_MODEL.equals(model) || IRON_MODEL.equals(model) || GOLD_MODEL.equals(model)
                    || DIAMOND_MODEL.equals(model) || NETHERITE_MODEL.equals(model)) cir.setReturnValue(true);
        }
    }

    @Inject(method = "getArrow", at = @At("HEAD"), cancellable = true)
    private void vanillaBowTiers$createRealTieredArrow(ItemStack projectile, float power,
            @Nullable ItemStack firingWeapon, CallbackInfoReturnable<AbstractArrow> cir) {
        AbstractSkeleton skeleton = (AbstractSkeleton)(Object)this;
        ItemStack bow = skeleton.getMainHandItem();
        if (!bow.is(Items.BOW)) return;
        vanillaBowTiers$assignAmmo(skeleton, bow);
        CustomModelData marker = bow.get(DataComponents.CUSTOM_MODEL_DATA);
        if (marker == null || marker.getString(0) == null) return;
        ArrowItem arrowItem = switch (marker.getString(0)) {
            case "copper" -> (ArrowItem) ModItems.COPPER_ARROW;
            case "iron" -> (ArrowItem) ModItems.IRON_ARROW;
            case "golden" -> (ArrowItem) ModItems.GOLDEN_ARROW;
            case "diamond" -> (ArrowItem) ModItems.DIAMOND_ARROW;
            case "netherite" -> (ArrowItem) ModItems.NETHERITE_ARROW;
            case "vanilla" -> (ArrowItem) Items.ARROW;
            default -> null;
        };
        if (arrowItem == null) return;
        Identifier model = bow.get(DataComponents.ITEM_MODEL);
        double bowMultiplier = IRON_MODEL.equals(model) ? 1.10D
                : DIAMOND_MODEL.equals(model) ? 1.20D : NETHERITE_MODEL.equals(model) ? 1.35D : 1.00D;
        double ammoMultiplier = arrowItem instanceof TieredArrowItem tiered ? tiered.damageMultiplier() : 1.0D;
        // Leave ordinary vanilla shots on vanilla's path, including potion-bearing ammo.
        if (arrowItem == Items.ARROW && bowMultiplier == 1.0D) return;
        AbstractArrow arrow = arrowItem.createArrow(skeleton.level(), new ItemStack(arrowItem), skeleton, firingWeapon);
        arrow.setBaseDamageFromMob((float)(power * ammoMultiplier * bowMultiplier));
        cir.setReturnValue(arrow);
    }
}