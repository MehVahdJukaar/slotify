package net.mehvahdjukaar.slotify.forge;

import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.slotify.Slotify;
import net.minecraftforge.fml.common.Mod;

/**
 * Author: MehVahdJukaar
 */
@Mod(Slotify.MOD_ID)
public class SlotifyForge {


    public SlotifyForge() {
        if (PlatHelper.getPhysicalSide().isClient()) {
            Slotify.clientInit();
        }
    }


}
