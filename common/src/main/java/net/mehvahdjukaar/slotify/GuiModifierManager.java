package net.mehvahdjukaar.slotify;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
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

    private static final Map<MenuType<?>, Int2ObjectArrayMap<SlotModifier>> SLOTS_BY_MENU_ID = new IdentityHashMap<>();
    private static final Map<Class<?>, Int2ObjectArrayMap<SlotModifier>> SLOTS_BY_CLASS = new IdentityHashMap<>();
    public static final Map<MenuType<?>, ScreenModifier> BY_MENU_ID = new IdentityHashMap<>();
    public static final Map<Class<?>, ScreenModifier> BY_CLASS = new IdentityHashMap<>();


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
                    BY_CLASS.merge(cl, new ScreenModifier(mod), (a, b) -> b.merge(a));

                    if (!mod.slotModifiers().isEmpty()) {
                        Int2ObjectArrayMap<SlotModifier> map = SLOTS_BY_CLASS.computeIfAbsent(cl,
                                i -> new Int2ObjectArrayMap<>());
                        unwrapSlots(mod, map);
                    }

                } catch (ClassNotFoundException ignored) {
                }


            } else {
                ResourceLocation menuId = new ResourceLocation(mod.target());
                boolean isInventory = menuId.equals(INVENTORY);
                Optional<MenuType<?>> menu = BuiltInRegistries.MENU.getOptional(menuId);

                if (menu.isPresent() || isInventory) {
                    BY_MENU_ID.merge(menu.orElse(null), new ScreenModifier(mod), (a, b) -> b.merge(a));

                    if (!mod.slotModifiers().isEmpty()) {
                        Int2ObjectArrayMap<SlotModifier> map = SLOTS_BY_MENU_ID.computeIfAbsent(menu.orElse(null),
                                i -> new Int2ObjectArrayMap<>());
                        unwrapSlots(mod, map);
                    }
                }
            }

        }
        Slotify.LOGGER.info("Loaded modifiers for: "+ SLOTS_BY_MENU_ID.keySet() + " "+
                SLOTS_BY_CLASS.keySet() + " " + BY_MENU_ID.keySet() + " "+ BY_CLASS.keySet());
    }

    private static void unwrapSlots(GuiModifier mod, Int2ObjectArrayMap<SlotModifier> map) {
        for (SlotModifier s : mod.slotModifiers()) {
            for (int i : s.targets().getSlots()) {
                //merging makes no sense we just keep last
                map.merge(i, s, SlotModifier::merge);
            }
        }
    }


    @Nullable
    public static ScreenModifier getGuiModifier(AbstractContainerScreen<?> screen) {
        var m = BY_CLASS.get(screen.getClass());
        m = getScreenModifier(screen);
        return m;
    }

    private static ScreenModifier getScreenModifier(AbstractContainerScreen<?> screen) {
        ScreenModifier m = null;
        AbstractContainerMenu menu = screen.getMenu();
        if(menu != null) {
            m = BY_CLASS.get(menu.getClass());
        }
        if (m == null) {
            MenuType<?> type;
            try {
                type = menu.getType();
            } catch (Exception e) {
                type = null;
            }
            m = BY_MENU_ID.get(type);
        }
        return m;
    }

    @Nullable
    public static ScreenModifier getGuiModifier(Screen screen) {
        var m = BY_CLASS.get(screen.getClass());
        if (m == null && screen instanceof AbstractContainerScreen<?> as) {
            m = getScreenModifier(as);
        }
        return m;
    }

    @Nullable
    public static SlotModifier getSlotModifier(AbstractContainerScreen<?> screen, Slot slot) {
        var m = SLOTS_BY_CLASS.get(screen.getClass());
        if (m == null) SLOTS_BY_CLASS.get(screen.getMenu().getClass());
        if (m == null) {
            MenuType<?> type;
            try {
                type = screen.getMenu().getType();
            } catch (Exception e) {
                type = null;
            }
            m = SLOTS_BY_MENU_ID.get(type);
        }
        if (m != null) {
            return m.get(slot.index);
        }
        return null;
    }

    @Nullable
    public static SlotModifier getSlotModifier(AbstractContainerMenu menu, Slot slot) {
        var m = SLOTS_BY_CLASS.get(menu.getClass());
        if (m == null) {
            MenuType<?> type;
            try {
                type = menu.getType();
            } catch (Exception e) {
                type = null;
            }
            m = SLOTS_BY_MENU_ID.get(type);
        }
        if (m != null) {
            return m.get(slot.index);
        }
        return null;
    }


    public static void maybeModifySlot(AbstractContainerMenu menu, Slot slot) {
        var mod = getSlotModifier(menu, slot);
        if (mod != null) {
            mod.modify(slot);
        }
    }

    public static boolean maybeChangeColor(AbstractContainerScreen<?> screen, Slot slot, GuiGraphics graphics,
                                           int x, int y, int offset) {
        var mod = getSlotModifier(screen, slot);
        if (mod != null && mod.hasCustomColor()) {
            mod.renderCustomHighlight(graphics, x, y, offset);
            return false;
        }
        return true;
    }
}
