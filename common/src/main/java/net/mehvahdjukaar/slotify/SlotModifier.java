package net.mehvahdjukaar.slotify;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.Slot;

public record SlotModifier(TargetSlots targets, int x, int y) {

    public static final Codec<SlotModifier> CODEC = RecordCodecBuilder.create(i -> i.group(
            TargetSlots.CODEC.fieldOf("slots").forGetter(SlotModifier::targets),
            Codec.INT.fieldOf("x_offset").forGetter(SlotModifier::x),
            Codec.INT.fieldOf("y_offset").forGetter(SlotModifier::y)
    ).apply(i, SlotModifier::new));

    public void modify(Slot slot) {
        slot.x += this.x;
        slot.y += this.y;
    }
}
