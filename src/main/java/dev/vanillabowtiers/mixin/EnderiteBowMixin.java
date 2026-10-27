package dev.vanillabowtiers.mixin;

import dev.vanillabowtiers.AmmoModelData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "net.enderitemc.enderitemod.tools.EnderiteBow", remap = false)
public abstract class EnderiteBowMixin {
    @Inject(method = "use", at = @At("HEAD"), remap = false)
    private void vanillaBowTiers$rememberSelectedAmmo(
            Level level, Player player, InteractionHand hand,
            CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack bow = player.getItemInHand(hand);
        ItemStack ammo = player.getProjectile(bow);

        AmmoModelData.set(bow, AmmoModelData.tier(ammo));
    }
}
