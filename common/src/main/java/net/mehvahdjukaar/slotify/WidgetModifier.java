package net.mehvahdjukaar.slotify;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ExtraCodecs;

import java.util.*;
import java.util.function.Function;

public record WidgetModifier(int xOffset, int yOffset,
                             int width,
                             Optional<String> message,
                             Optional<Integer> targetX, Optional<Integer> targetY,
                             Optional<Integer> targetW, Optional<Integer> targetH,
                             Optional<String> targetMessage,
                             Optional<String> targetClass) {

    public static final Codec<WidgetModifier> CODEC = RecordCodecBuilder.<WidgetModifier>create(i -> i.group(
            Codec.INT.optionalFieldOf("x_offset", 0).forGetter(WidgetModifier::xOffset),
            Codec.INT.optionalFieldOf("y_offset", 0).forGetter(WidgetModifier::yOffset),
            Codec.INT.optionalFieldOf("width_increment", 0).forGetter(WidgetModifier::width),
            Codec.STRING.optionalFieldOf("message").forGetter(WidgetModifier::message),

            Codec.INT.optionalFieldOf("target_x").forGetter(WidgetModifier::targetX),
            Codec.INT.optionalFieldOf("target_y").forGetter(WidgetModifier::targetY),
            Codec.INT.optionalFieldOf("target_width").forGetter(WidgetModifier::targetY),
            Codec.INT.optionalFieldOf("target_height").forGetter(WidgetModifier::targetY),
            Codec.STRING.optionalFieldOf("target_message").forGetter(WidgetModifier::targetMessage),
            Codec.STRING.xmap(PlatStuff::remapName, PlatStuff::remapName).optionalFieldOf("target_class_name").forGetter(WidgetModifier::targetClass)
    ).apply(i, WidgetModifier::new)).comapFlatMap(o -> {
        if (o.targetW.isEmpty() && o.targetH.isEmpty() && o.targetX.isEmpty()
                && o.targetClass.isEmpty()
                && o.targetY.isEmpty() && o.targetMessage.isEmpty()) {
            return DataResult.error("Widget modifier must have at least one target");
        }
        return DataResult.success(o);
    }, Function.identity());

    public void maybeModify(AbstractWidget widget) {
        if (targetX.isPresent() && widget.x != targetX.get()) return;
        if (targetY.isPresent() && widget.y != targetY.get()) return;
        if (targetH.isPresent() && widget.getHeight() != targetH.get()) return;
        if (targetW.isPresent() && widget.getWidth() != targetW.get()) return;
        if (targetMessage.isPresent() && !widget.getMessage().getString().equals(targetMessage.get())) return;
        if (targetClass.isPresent()) {
            String name = targetClass.get();
            if (!widget.getClass().getSimpleName().equals(name) &&
                    !widget.getClass().getName().equals(name)) return;
        }
        widget.x += this.xOffset;
        widget.y += this.yOffset;
        widget.setWidth(widget.getWidth() + this.width);

        message.ifPresent(s -> widget.setMessage(Component.translatable(s)));
    }
}
