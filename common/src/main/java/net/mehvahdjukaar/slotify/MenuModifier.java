package net.mehvahdjukaar.slotify;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public record MenuModifier(ResourceLocation menuId, List<SlotModifier> modifiers) {

    public static final Codec<MenuModifier> CODEC = RecordCodecBuilder.create(i -> i.group(
            ResourceLocation.CODEC.fieldOf("menu_id").forGetter(MenuModifier::menuId),
            SlotModifier.CODEC.listOf().fieldOf("modifiers").forGetter(MenuModifier::modifiers)
    ).apply(i, MenuModifier::new));


}
