package net.mehvahdjukaar.slotify.forge;

import cpw.mods.modlauncher.api.INameMappingService;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

public class PlatStuffImpl {
    public static String remapName(String string) {
        return ObfuscationReflectionHelper.remapName(INameMappingService.Domain.CLASS, string);
    }
}
