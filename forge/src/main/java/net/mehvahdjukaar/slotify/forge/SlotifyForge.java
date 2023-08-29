package net.mehvahdjukaar.slotify.forge;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mehvahdjukaar.slotify.GuiModifierManager;
import net.mehvahdjukaar.slotify.Slotify;
import net.mehvahdjukaar.slotify.SlotifyScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Author: MehVahdJukaar
 */
@Mod(Slotify.MOD_ID)
public class SlotifyForge {


    public SlotifyForge() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(SlotifyForge::registerReload);
        MinecraftForge.EVENT_BUS.addListener(SlotifyForge::renderScreen);
    }


    public static void registerReload(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new GuiModifierManager());
    }

    public static void renderScreen(ScreenEvent.Render.Post event){
        Screen screen = event.getScreen();
        SlotifyScreen ss = (SlotifyScreen) screen;
        if(ss.slotify$hasSprites()) {

            PoseStack poseStack = event.getPoseStack();
            poseStack.translate(screen.width / 2F, screen.height / 2F, screen.getBlitOffset());

            ss.slotify$renderExtraSprites(poseStack);
        }
    }


}
