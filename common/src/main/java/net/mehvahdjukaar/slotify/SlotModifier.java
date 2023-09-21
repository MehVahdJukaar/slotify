package net.mehvahdjukaar.slotify;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.EnderChestBlock;

import java.util.*;

public record SlotModifier(TargetSlots targets, int color, int color2, int xOffset, int yOffset, int zOffset,
                           Optional<Integer> targetX, Optional<Integer> targetY, Optional<String> targetClass) {

    public static final Codec<SlotModifier> CODEC = RecordCodecBuilder.create(i -> i.group(
            TargetSlots.CODEC.fieldOf("slots").forGetter(SlotModifier::targets),
            ColorUtils.CODEC.optionalFieldOf("color", -1).forGetter(SlotModifier::color),
            ColorUtils.CODEC.optionalFieldOf("color_2", -1).forGetter(SlotModifier::color2),
            Codec.INT.optionalFieldOf("x_offset", 0).forGetter(SlotModifier::xOffset),
            Codec.INT.optionalFieldOf("y_offset", 0).forGetter(SlotModifier::yOffset),
            Codec.INT.optionalFieldOf("z_offset", 0).forGetter(SlotModifier::zOffset),
            Codec.INT.optionalFieldOf("target_x").forGetter(SlotModifier::targetX),
            Codec.INT.optionalFieldOf("target_y").forGetter(SlotModifier::targetY),
            Codec.STRING.xmap(PlatStuff::remapName, PlatStuff::remapName).optionalFieldOf("target_class_name").forGetter(SlotModifier::targetClass)
    ).apply(i, SlotModifier::new));
    private final TargetSlots targets;
    private final int color;
    private final int color2;
    private final int xOffset;
    private final int yOffset;
    private final Optional<Integer> targetX;
    private final Optional<Integer> targetY;
    private final Optional<String> targetClass;

    public SlotModifier(TargetSlots targets, int color, int color2, int xOffset, int yOffset,
                        Optional<Integer> targetX, Optional<Integer> targetY, Optional<String> targetClass) {
        this.targets = targets;
        this.color = color;
        this.color2 = color2;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetClass = targetClass;
    }

    public void modify(Slot slot) {
        if (targetX.isPresent() && slot.x != targetX.get()) return;
        if (targetY.isPresent() && slot.y != targetY.get()) return;
        if (targetClass.isPresent()) {
            String name = targetClass.get();
            if (!slot.getClass().getSimpleName().equals(name) &&
                    !slot.getClass().getName().equals(name)) return;
        }
        slot.x += this.xOffset;
        slot.y += this.yOffset;
    }

    public boolean hasCustomColor() {
        return color != -1 || color2 != -1 || zOffset != 0;
    }

    public void renderCustomHighlight(PoseStack graphics, int x, int y, int offset) {
        int c1 = color;
        int c2 = color2 == -1 ? color : color2;
        renderSlotHighlight2(graphics, x, y, c1, c2, offset + zOffset);
    }

    public static void renderSlotHighlight2(PoseStack graphics, int x, int y,
                                            int slotColor, int slotColor2, int offset) {
        RenderSystem.disableDepthTest();
        RenderSystem.colorMask(true, true, true, false);
        fillGradient(graphics, x, y, x + 16, y + 16, slotColor, slotColor2, offset);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.enableDepthTest();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (SlotModifier) obj;
        return Objects.equals(this.targets, that.targets) &&
                this.color == that.color &&
                this.color2 == that.color2 &&
                this.xOffset == that.xOffset &&
                this.yOffset == that.yOffset;
    }

    @Override
    public String toString() {
        return "SlotModifier[" +
                "targets=" + targets + ", " +
                "color=" + color + ", " +
                "color2=" + color2 + ", " +
                "xOffset=" + xOffset + ", " +
                "yOffset=" + yOffset + ']';
    }

    public boolean hasOffset() {
        return xOffset != 0 || yOffset != 0;
    }

    public SlotModifier merge(SlotModifier other) {
        Set<Integer> combinedSlots = new HashSet<>();

        this.targets.getSlots().forEach(combinedSlots::add);
        other.targets.getSlots().forEach(combinedSlots::add);

        return new SlotModifier(new TargetSlots.ListTarget(new ArrayList<>(combinedSlots)),
                other.hasCustomColor() ? other.color : this.color,
                other.hasCustomColor() ? other.color2 : this.color,
                other.hasOffset() ? other.xOffset : this.xOffset,
                other.hasOffset() ? other.yOffset : this.yOffset,
                other.zOffset,
                other.targetX,
                other.targetY,
                other.targetClass
        );
    }

    public TargetSlots targets() {
        return targets;
    }

    public int color() {
        return color;
    }

    public int color2() {
        return color2;
    }

    public int xOffset() {
        return xOffset;
    }

    public int yOffset() {
        return yOffset;
    }

    public Optional<Integer> targetX() {
        return targetX;
    }

    public Optional<Integer> targetY() {
        return targetY;
    }

    public Optional<String> targetClass() {
        return targetClass;
    }

    @Override
    public int hashCode() {
        return Objects.hash(targets, color, color2, xOffset, yOffset, targetX, targetY, targetClass);
    }

}
