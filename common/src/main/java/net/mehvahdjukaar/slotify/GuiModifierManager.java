package net.mehvahdjukaar.slotify;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GuiModifierManager extends SimpleJsonResourceReloadListener {

    private static final Map<MenuType<?>, Int2ObjectArrayMap<List<SlotModifier>>> SLOTS_BY_MENU_ID = new IdentityHashMap<>();
    private static final Map<Class<?>, Int2ObjectArrayMap<List<SlotModifier>>> SLOTS_BY_CLASS = new IdentityHashMap<>();
    public static final Map<MenuType<?>, GuiModifier> BY_MENU_ID = new IdentityHashMap<>();
    public static final Map<Class<?>, GuiModifier> BY_CLASS = new IdentityHashMap<>();


    private static final ResourceLocation INVENTORY = new ResourceLocation("inventory");


    public GuiModifierManager() {
        super(new GsonBuilder().setPrettyPrinting().create(), "gui_modifiers");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        SLOTS_BY_MENU_ID.clear();
        SLOTS_BY_CLASS.clear();
        BY_MENU_ID.clear();
        BY_CLASS.clear();

        List<GuiModifier> allModifiers = new ArrayList<>();
        for (var o : object.values()) {
            var result = GuiModifier.CODEC.parse(JsonOps.INSTANCE, o);
            GuiModifier modifier = result.getOrThrow(false, e -> Slotify.LOGGER.error("Failed to parse menu modifier: {}", e));
            allModifiers.add(modifier);
        }

        for (GuiModifier mod : allModifiers) {
            //inventory has a null menu type for some reason

            if (mod.targetsClass()) {
                try {
                    var cl = Class.forName(mod.target());
                    BY_CLASS.put(cl, mod);

                    if(!mod.slotModifiers().isEmpty()) {
                        Int2ObjectArrayMap<List<SlotModifier>> map = SLOTS_BY_CLASS.computeIfAbsent(cl,
                                i -> new Int2ObjectArrayMap<>());
                        mergeSlotMods(mod, map);
                    }

                } catch (ClassNotFoundException ignored) {
                }


            } else {
                ResourceLocation menuId = new ResourceLocation(mod.target());
                boolean isInventory = menuId.equals(INVENTORY);
                Optional<MenuType<?>> menu = Registry.MENU.getOptional(menuId);

                if (menu.isPresent() || isInventory) {
                    BY_MENU_ID.put(menu.orElse(null), mod);

                    if(!mod.slotModifiers().isEmpty()) {
                        Int2ObjectArrayMap<List<SlotModifier>> map = SLOTS_BY_MENU_ID.computeIfAbsent(menu.orElse(null),
                                i -> new Int2ObjectArrayMap<>());
                        mergeSlotMods(mod, map);
                    }
                }
            }

        }
    }

    private static void mergeSlotMods(GuiModifier mod, Int2ObjectArrayMap<List<SlotModifier>> map) {
        for (SlotModifier s : mod.slotModifiers()) {
            for (int i : s.targets().getSlots()) {
                var l = map.computeIfAbsent(i, integer -> new ArrayList<>());
                l.add(s);
            }
        }
    }

    public static List<SlotModifier> getModifier(AbstractContainerScreen<?> screen, Slot slot) {
        MenuType<?> type;
        try {
            type = screen.getMenu().getType();
        } catch (Exception e) {
            type = null;
        }
        return getModifier(screen.getClass(), type, slot);
    }

    @Nullable
    public static List<SlotModifier> getModifier(Class<?> clazz, @Nullable MenuType<?> type, Slot slot) {
        var m = SLOTS_BY_MENU_ID.get(type);
        if (m == null) SLOTS_BY_CLASS.get(clazz);
        if (m != null) {
            var l = m.get(slot.index);
            if (l != null) {
                return l;
            }
        }
        return null;
    }


    public static void maybeModifySlot(AbstractContainerMenu menu, Slot slot) {
        var l = getModifier(menu.getClass(), menu.getType(), slot);
        if (l != null) {
            for (var mod : l) {
                mod.modify(slot);
            }
        }
    }



    public static boolean maybeChangeColor(AbstractContainerScreen<?> screen, Slot slot, PoseStack graphics,
                                           int x, int y, int offset) {
        var l = getModifier(screen, slot);
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
