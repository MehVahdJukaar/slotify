package net.mehvahdjukaar.slotify.fabric;

import com.google.common.base.Suppliers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.mehvahdjukaar.slotify.MenuModifierManager;
import net.mehvahdjukaar.slotify.Slotify;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class SlotifyFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        addClientReloadListener(MenuModifierManager::new, Slotify.res("slot_modifiers"));
    }

    public static void addClientReloadListener(Supplier<PreparableReloadListener> listener, ResourceLocation name) {

        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new IdentifiableResourceReloadListener() {
            private final Supplier<PreparableReloadListener> inner = Suppliers.memoize(listener::get);

            @Override
            public ResourceLocation getFabricId() {
                return name;
            }

            @Override
            public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager,
                                                  ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler,
                                                  Executor backgroundExecutor, Executor gameExecutor) {
                return inner.get().reload(preparationBarrier, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
            }
        });
    }

}
