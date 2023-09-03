package net.mehvahdjukaar.slotify.mixins;

import net.mehvahdjukaar.slotify.SlotifyScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import top.theillusivec4.curios.client.gui.CuriosScreen;

@Pseudo
@Mixin(CuriosScreen.class)
public abstract class CurioInventoryScreenMixin {

    @ModifyArgs( method =  "m_7286_", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/inventory/InventoryScreen;m_274545_(Lnet/minecraft/client/gui/GuiGraphics;IIIFFLnet/minecraft/world/entity/LivingEntity;)V")
    )
    public void modifyRenderEntityI(Args args) {
        var m = ((SlotifyScreen) this).slotify$getModifier();
        if (m != null) {
            var s = m.getSpecial("player");
            if (s != null) {
                args.set(0, (int) args.get(0) + s.x());
                args.set(1, (int) args.get(1) + s.y());
            }
        }
    }

}
