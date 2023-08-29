package net.mehvahdjukaar.slotify.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mehvahdjukaar.slotify.GuiModifierManager;
import net.mehvahdjukaar.slotify.ScreenModifier;
import net.mehvahdjukaar.slotify.SlotifyScreen;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class ScreenMixin implements SlotifyScreen {

    @Unique
    private ScreenModifier slotify$modifier = null;

    @Inject(method = "init()V", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        slotify$modifier = GuiModifierManager.getGuiModifier((Screen) (Object) this);
    }

    @Override
    public void slotify$renderExtraSprites(PoseStack poseStack) {
        if (slotify$modifier != null) slotify$modifier.sprites().forEach(r -> r.render(poseStack));
    }

    @Override
    public boolean slotify$hasSprites() {
        return slotify$modifier != null && !slotify$modifier.sprites().isEmpty();
    }

    @Override
    public ScreenModifier slotify$getModifier() {
        return slotify$modifier;
    }
}
