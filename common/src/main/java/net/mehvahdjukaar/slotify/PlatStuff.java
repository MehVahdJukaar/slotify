package net.mehvahdjukaar.slotify;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class PlatStuff {
    @ExpectPlatform
    public static String maybeRemapName(String s) {
        throw new AssertionError();
    }
}
