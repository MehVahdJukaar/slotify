package net.mehvahdjukaar.slotify.forge;

import net.mehvahdjukaar.slotify.MenuModifierManager;
import net.mehvahdjukaar.slotify.Slotify;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Author: MehVahdJukaar
 */
@Mod(Slotify.MOD_ID)
public class SlotifyForge {


    public SlotifyForge() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(SlotifyForge::registerReload);
    }


    public static void registerReload(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new MenuModifierManager());
    }


}
