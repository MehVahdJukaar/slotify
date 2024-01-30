package net.mehvahdjukaar.slotify;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;
import java.util.Optional;

public record SimpleSprite(ResourceLocation texture, float x, float y, float width, float height, float z){//, Optional<ScreenSupplier> screenSupp) {

    public static final Codec<SimpleSprite> CODEC = RecordCodecBuilder.create(i -> i.group(
            ResourceLocation.CODEC.fieldOf("texture").forGetter(SimpleSprite::texture),
            Codec.FLOAT.fieldOf("x").forGetter(SimpleSprite::x),
            Codec.FLOAT.fieldOf("y").forGetter(SimpleSprite::y),
            Codec.FLOAT.fieldOf("width").forGetter(SimpleSprite::width),
            Codec.FLOAT.fieldOf("height").forGetter(SimpleSprite::height),
            Codec.FLOAT.optionalFieldOf("z", 0.0f).forGetter(SimpleSprite::z)
    ).apply(i, SimpleSprite::new));

    public void render(PoseStack poseStack) {
        RenderSystem.setShaderTexture(0, texture);
        innerBlit(poseStack.last().pose(), x, x+width, y, y+height, z);
    }

    private static void innerBlit(Matrix4f matrix, float x1, float x2, float y1, float y2, float blitOffset) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.vertex(matrix, x1, y2, blitOffset).uv(0, 1).endVertex();
        bufferBuilder.vertex(matrix, x2, y2, blitOffset).uv(1, 1).endVertex();
        bufferBuilder.vertex(matrix, x2, y1, blitOffset).uv(1, 0).endVertex();
        bufferBuilder.vertex(matrix, x1, y1, blitOffset).uv(0, 0).endVertex();
        BufferUploader.drawWithShader(bufferBuilder.end());
    }
}
