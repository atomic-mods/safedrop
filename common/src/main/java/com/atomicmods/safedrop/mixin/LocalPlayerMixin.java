package com.atomicmods.safedrop.mixin;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {

    @Unique
    private long atomic_safedrop$lastDropAttemptTime = 0;

    @Inject(method = "drop", at = @At("HEAD"), cancellable = true)
    private void atomic_safedrop$onDrop(boolean fullStack, CallbackInfoReturnable<Boolean> cir) {
        LocalPlayer player = (LocalPlayer) (Object) this;
        ItemStack heldItem = player.getMainHandItem();

        if (!heldItem.isEmpty()) {
            boolean isValuable = heldItem.isEnchanted() || heldItem.has(net.minecraft.core.component.DataComponents.CUSTOM_NAME);

            if (isValuable) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - atomic_safedrop$lastDropAttemptTime > 500) {
                    player.displayClientMessage(Component.translatable("message.atomic_safedrop.press_again"), true);
                    atomic_safedrop$lastDropAttemptTime = currentTime;
                    cir.setReturnValue(false);
                } else {
                    atomic_safedrop$lastDropAttemptTime = 0; // Reset after successfully dropping
                }
            }
        }
    }
}
