package net.mehvahdjukaar.slotify.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import net.mehvahdjukaar.slotify.GuiModifierManager;
import net.mehvahdjukaar.slotify.ScreenModifier;
import net.mehvahdjukaar.slotify.SlotifyScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public class InventoryScreenMixin {
    @Inject(method = "renderBg", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/inventory/InventoryScreen;blit(Lcom/mojang/blaze3d/vertex/PoseStack;IIIIII)V",
            shift = At.Shift.AFTER))
    public void modifyRenderEntity(PoseStack poseStack, float partialTick, int mouseX, int mouseY, CallbackInfo ci,
                                   @Local(ordinal = 2) LocalIntRef i, @Local(ordinal = 3) LocalIntRef j) {
        var m = ((SlotifyScreen)this).slotify$getModifier();
        if (m != null) {
            var s = m.getSpecial("player");
            if (s != null) {
                i.set(i.get() + s.x());
                j.set(j.get() + s.y());
            }
        }
    }
}
