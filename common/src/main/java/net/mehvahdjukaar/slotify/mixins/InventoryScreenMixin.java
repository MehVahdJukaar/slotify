package net.mehvahdjukaar.slotify.mixins;

import net.mehvahdjukaar.slotify.SlotifyScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin {

    @ModifyArgs( method = "renderBg", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/inventory/InventoryScreen;renderEntityInInventoryFollowsMouse(Lnet/minecraft/client/gui/GuiGraphics;IIIFFLnet/minecraft/world/entity/LivingEntity;)V"))
    public void modifyRenderEntityI(Args args) {
        var m = ((SlotifyScreen) this).slotify$getModifier();
        if (m != null) {
            var s = m.getSpecial("player");
            if (s != null) {
                args.set(0, (int) args.get(1) + s.x());
                args.set(1, (int) args.get(2) + s.y());
            }
        }
    }

}
