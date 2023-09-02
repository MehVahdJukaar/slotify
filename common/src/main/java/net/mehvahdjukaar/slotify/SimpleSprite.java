package net.mehvahdjukaar.slotify;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.player.Player;
import org.joml.Matrix4f;

import java.util.Optional;

public record SimpleSprite(ResourceLocation texture, int x, int y, int width, int height, int z,
                           Optional<String> tooltip, Optional<ScreenSupplier> screenSupp) {

    public static final Codec<SimpleSprite> CODEC = RecordCodecBuilder.create(i -> i.group(
            ResourceLocation.CODEC.fieldOf("texture").forGetter(SimpleSprite::texture),
            Codec.INT.fieldOf("x").forGetter(SimpleSprite::x),
            Codec.INT.fieldOf("y").forGetter(SimpleSprite::y),
            Codec.INT.fieldOf("width").forGetter(SimpleSprite::width),
            Codec.INT.fieldOf("height").forGetter(SimpleSprite::height),
            Codec.INT.optionalFieldOf("z_offset", 0).forGetter(SimpleSprite::z),
            Codec.STRING.optionalFieldOf("tooltip").forGetter(SimpleSprite::tooltip),
            Codec.STRING.xmap(ScreenSupplier::decode, ScreenSupplier::toString)
                    .optionalFieldOf("screen_class").forGetter(SimpleSprite:: screenSupp)
    ).apply(i, SimpleSprite::new));


    class  ScreenSupplier{
        private static Optional<ScreenSupplier> decode(String s) {
            try {
                var cl =   Class.forName(PlatStuff.remapName(s));
                cl.g
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

        }

        @Override
        public String toString() {
            return super.toString();
        }
    }

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
