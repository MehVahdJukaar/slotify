package net.mehvahdjukaar.slotify.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mehvahdjukaar.slotify.GuiModifierManager;
import net.mehvahdjukaar.slotify.SimpleSprite;
import net.mehvahdjukaar.slotify.SlotifyScreen;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(Screen.class)
public abstract class ScreenMixin implements SlotifyScreen {

    @Unique
    private final List<SimpleSprite> slotify$extraSprites = new ArrayList<>();

    @Inject(method = "init()V", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        slotify$extraSprites.addAll(GuiModifierManager.getExtraSprites((Screen) (Object) this));
    }

    @Override
    public void slotify$renderExtraSprites(PoseStack poseStack) {
        slotify$extraSprites.forEach(r -> r.render(poseStack));
    }

    @Override
    public boolean slotify$hasSprites() {
        return !slotify$extraSprites.isEmpty();
    }
}
