package net.mehvahdjukaar.slotify;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record SimpleSprite(ResourceLocation texture, int x, int y, int width, int height, int z) {

    public static final Codec<SimpleSprite> CODEC = RecordCodecBuilder.create(i -> i.group(
            ResourceLocation.CODEC.fieldOf("texture").forGetter(SimpleSprite::texture),
            Codec.INT.fieldOf("x").forGetter(SimpleSprite::x),
            Codec.INT.fieldOf("y").forGetter(SimpleSprite::y),
            Codec.INT.fieldOf("width").forGetter(SimpleSprite::width),
            Codec.INT.fieldOf("height").forGetter(SimpleSprite::height),
            Codec.INT.optionalFieldOf("z_offset", 0).forGetter(SimpleSprite::z)
    ).apply(i, SimpleSprite::new));
}
