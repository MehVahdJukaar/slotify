package net.mehvahdjukaar.slotify;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record MenuModifier(ResourceLocation menuId, List<SlotModifier> modifiers,
                           int titleX, int titleY, int labelX, int labelY) {

    public static final Codec<MenuModifier> CODEC = RecordCodecBuilder.create(i -> i.group(
            ResourceLocation.CODEC.fieldOf("menu_id").forGetter(MenuModifier::menuId),
            SlotModifier.CODEC.listOf().fieldOf("modifiers").forGetter(MenuModifier::modifiers),
            Codec.INT.optionalFieldOf("title_x_offset", 0).forGetter(MenuModifier::titleX),
            Codec.INT.optionalFieldOf("title_y_offset", 0).forGetter(MenuModifier::titleY),
            Codec.INT.optionalFieldOf("label_x_offset", 0).forGetter(MenuModifier::labelX),
            Codec.INT.optionalFieldOf("label_y_offset", 0).forGetter(MenuModifier::labelY)
    ).apply(i, MenuModifier::new));


}
