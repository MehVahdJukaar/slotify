package net.mehvahdjukaar.slotify;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public record SimpleSprite(ResourceLocation texture, int x, int y, int width, int height, int z) {

    public static final Codec<SimpleSprite> CODEC = RecordCodecBuilder.create(i -> i.group(
            ResourceLocation.CODEC.fieldOf("texture").forGetter(SimpleSprite::texture),
            Codec.INT.fieldOf("x").forGetter(SimpleSprite::x),
            Codec.INT.fieldOf("y").forGetter(SimpleSprite::y),
            Codec.INT.fieldOf("width").forGetter(SimpleSprite::width),
            Codec.INT.fieldOf("height").forGetter(SimpleSprite::height),
            Codec.INT.optionalFieldOf("z_offset", 0).forGetter(SimpleSprite::z)
    ).apply(i, SimpleSprite::new));

    public void render(PoseStack poseStack) {
        RenderSystem.setShaderTexture(0, texture);
        innerBlit(poseStack.last().pose(), x, x+width, y, y+height, z);
    }

    private static void innerBlit(Matrix4f matrix, int x1, int x2, int y1, int y2, int blitOffset) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.vertex(matrix, (float)x1, (float)y2, (float)blitOffset).uv(0, 1).endVertex();
        bufferBuilder.vertex(matrix, (float)x2, (float)y2, (float)blitOffset).uv(1, 1).endVertex();
        bufferBuilder.vertex(matrix, (float)x2, (float)y1, (float)blitOffset).uv(1, 0).endVertex();
        bufferBuilder.vertex(matrix, (float)x1, (float)y1, (float)blitOffset).uv(0, 0).endVertex();
        BufferUploader.drawWithShader(bufferBuilder.end());
    }
}
