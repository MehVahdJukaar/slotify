package net.mehvahdjukaar.slotify.mixins;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.mehvahdjukaar.slotify.MenuModifierManager;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin<T extends AbstractContainerMenu> extends Screen implements MenuAccess<T> {

    @Shadow @Final protected T menu;

    protected AbstractContainerScreenMixin(Component component) {
        super(component);
    }

    @WrapWithCondition(method = "render", at = @At(
            target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderSlotHighlight(Lcom/mojang/blaze3d/vertex/PoseStack;III)V",
            value = "INVOKE"
    ))
    public boolean slotifyColor(PoseStack poseStack, int x, int y, int blitOffset,
                                @Local Slot slot){
        MenuType<?> type;
        try{
            type = this.menu.getType();
        }catch (Exception e){
            type = null;
        }
        return MenuModifierManager.maybeChangeColor(type, slot, poseStack, x, y, blitOffset);
    }
}
