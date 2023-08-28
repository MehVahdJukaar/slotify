package net.mehvahdjukaar.slotify.mixins;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.mehvahdjukaar.slotify.MenuModifier;
import net.mehvahdjukaar.slotify.MenuModifierManager;
import net.minecraft.client.gui.GuiGraphics;
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
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.mehvahdjukaar.slotify.MenuModifierManager.MENU_MODIFIERS;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin<T extends AbstractContainerMenu> extends Screen implements MenuAccess<T> {

    @Shadow
    @Final
    protected T menu;

    @Shadow
    protected int titleLabelY;

    @Shadow
    protected int titleLabelX;

    @Shadow
    protected int inventoryLabelY;

    @Shadow
    protected int inventoryLabelX;

    protected AbstractContainerScreenMixin(Component component) {
        super(component);
    }

    @WrapWithCondition(method = "render", at = @At(
            target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderSlotHighlight(Lnet/minecraft/client/gui/GuiGraphics;III)V",
            value = "INVOKE"
    ))
    public boolean slotifyColor(GuiGraphics guiGraphics, int x, int y, int blitOffset,
                                @Local Slot slot) {
        MenuType<?> type;
        try {
            type = this.menu.getType();
        } catch (Exception e) {
            type = null;
        }
        return MenuModifierManager.maybeChangeColor(type, slot, guiGraphics, x, y, blitOffset);
    }

    // this could be done with events...
    @Inject(method = "init", at = @At("TAIL"))
    public void modifyLabels(CallbackInfo ci) {
        MenuType<?> type;
        try {
            type = this.menu.getType();
        } catch (Exception e) {
            type = null;
        }
        //ugly, but we need to access these fields
        MenuModifier m = MENU_MODIFIERS.get(type);
        if (m != null) {
            this.titleLabelX += m.titleX();
            this.titleLabelY += m.titleY();
            this.inventoryLabelX += m.labelX();
            this.inventoryLabelY += m.labelY();
        }
    }
}
