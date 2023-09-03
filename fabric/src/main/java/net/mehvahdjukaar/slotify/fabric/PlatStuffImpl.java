package net.mehvahdjukaar.slotify.fabric;

import net.fabricmc.loader.api.FabricLoader;

public class PlatStuffImpl {

    public static String maybeRemapName(String s) {
        return FabricLoader.getInstance().getMappingResolver().mapClassName("official", s);

    }
}
