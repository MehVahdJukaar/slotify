package net.mehvahdjukaar.slotify;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MenuModifierManager extends SimpleJsonResourceReloadListener {

    private static final Map<MenuType<?>, Int2ObjectArrayMap<List<SlotModifier>>> MODIFIERS = new IdentityHashMap<>();
    private static final ResourceLocation INVENTORY = new ResourceLocation("inventory");


    public MenuModifierManager() {
        super(new GsonBuilder().setPrettyPrinting().create(), "menu_modifiers");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        MODIFIERS.clear();
        List<MenuModifier> list = new ArrayList<>();
        for (var o : object.values()) {
            var result = MenuModifier.CODEC.parse(JsonOps.INSTANCE, o);
            MenuModifier modifier = result.getOrThrow(false, e -> Slotify.LOGGER.error("Failed to parse menu modifier: {}", e));
            list.add(modifier);
        }
        for (var mod : list) {
            //inventory has a null menu type for some reason
            boolean isInventory = mod.menuId().equals(INVENTORY);
            Optional<MenuType<?>> menu = BuiltInRegistries.MENU.getOptional(mod.menuId());
            if (menu.isPresent() || isInventory) {
                Int2ObjectArrayMap<List<SlotModifier>> map = MODIFIERS.computeIfAbsent(menu.orElse(null),
                        i -> new Int2ObjectArrayMap<>());
                for (SlotModifier s : mod.modifiers()) {
                    for (int i : s.targets().getSlots()) {
                        var l = map.computeIfAbsent(i, integer -> new ArrayList<>());
                        l.add(s);
                    }
                }
            }
        }
    }


    public static void maybeModifySlot(@Nullable MenuType<?> type, Slot slot) {
        var l = getModifier(type, slot);
        if (l != null) {
            for (var mod : l) {
                mod.modify(slot);
            }
        }
    }

    @Nullable
    public static List<SlotModifier> getModifier(@Nullable MenuType<?> type, Slot slot) {
        var m = MODIFIERS.get(type);
        if (m != null) {
            var l = m.get(slot.index);
            if (l != null) {
                return l;
            }
        }
        return null;
    }


    public static boolean maybeChangeColor(MenuType<?> type, Slot slot, GuiGraphics graphics,
                                           int x, int y, int offset) {
        var l = getModifier(type, slot);
        if (l != null) {
            for (var mod : l) {
                if (mod.hasCustomColor()) {
                    mod.renderCustomHighlight(graphics, x, y, offset);
                    return false;
                }
            }
        }
        return true;
    }

}
