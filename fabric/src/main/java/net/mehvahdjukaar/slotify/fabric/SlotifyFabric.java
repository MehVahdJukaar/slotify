package net.mehvahdjukaar.slotify.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.slotify.Slotify;

public class SlotifyFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        if(PlatHelper.getPhysicalSide().isClient()) {
            Slotify.clientInit();
        }
    }
}
