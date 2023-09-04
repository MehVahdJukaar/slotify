package net.mehvahdjukaar.slotify.forge;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.mehvahdjukaar.slotify.GuiModifierManager;
import net.mehvahdjukaar.slotify.Slotify;
import net.mehvahdjukaar.slotify.SlotifyScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

/**
 * Author: MehVahdJukaar
 */
@Mod(Slotify.MOD_ID)
public class SlotifyForge {


    public SlotifyForge() {
        if(FMLEnvironment.dist == Dist.CLIENT) {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(SlotifyForge::registerReload);
            MinecraftForge.EVENT_BUS.addListener(SlotifyForge::renderScreen);
        }else{
            Slotify.LOGGER.warn("Slotify has been installed on a server. This wont cause issues but mod wont do anything here as its a client mod");
        }
    }


    public static void registerReload(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new GuiModifierManager());
    }

    public static void renderScreen(ScreenEvent.Render.Post event){
        Screen screen = event.getScreen();
        SlotifyScreen ss = (SlotifyScreen) screen;
        if(ss.slotify$hasSprites()) {

            PoseStack poseStack = event.getGuiGraphics().pose();
            poseStack.pushPose();
            poseStack.translate(screen.width / 2F, screen.height / 2F, 500);
            ss.slotify$renderExtraSprites(poseStack);
            poseStack.popPose();
        }
    }


}
