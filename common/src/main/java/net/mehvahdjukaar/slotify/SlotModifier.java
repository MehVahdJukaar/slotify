package net.mehvahdjukaar.slotify;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.inventory.Slot;

public record SlotModifier(TargetSlots targets, int color, int color2, int xOffset, int yOffset) {

    public static final Codec<SlotModifier> CODEC = RecordCodecBuilder.create(i -> i.group(
            TargetSlots.CODEC.fieldOf("slots").forGetter(SlotModifier::targets),
            Codec.INT.optionalFieldOf("color", -1).forGetter(SlotModifier::color),
            Codec.INT.optionalFieldOf("color_2", -1).forGetter(SlotModifier::color2),
            Codec.INT.optionalFieldOf("x_offset", 0).forGetter(SlotModifier::xOffset),
            Codec.INT.optionalFieldOf("y_offset", 0).forGetter(SlotModifier::yOffset)
    ).apply(i, SlotModifier::new));

    public void modify(Slot slot) {
        slot.x += this.xOffset;
        slot.y += this.yOffset;
    }

    public boolean hasCustomColor() {
        return color != -1 || color2 != -1;
    }

    public void renderCustomHighlight(GuiGraphics guiGraphics, int x, int y, int offset) {
        int c1 = color;
        int c2 = color2 == -1 ? color : color2;
        guiGraphics.fillGradient(RenderType.guiOverlay(), x, y, x + 16, y + 16,
                c1, c2, offset);
    }
}
