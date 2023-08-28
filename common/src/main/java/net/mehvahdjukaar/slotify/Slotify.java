package net.mehvahdjukaar.slotify;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Slotify {

    public static final String MOD_ID = "slotify";

    public static final Logger LOGGER = LogManager.getLogger("Slotify");

    public static ResourceLocation res(String n) {
        return new ResourceLocation(MOD_ID, n);
    }

    public static String str(String n) {
        return MOD_ID + ":" + n;
    }



}
