package net.conczin.immersive_pillagers.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.conczin.immersive_pillagers.ImmersivePillagers;
import net.conczin.immersive_pillagers.entity.UndeadEvoker;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.IllagerRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public class UndeadEvokerRenderer extends IllagerRenderer<UndeadEvoker> {
    private static final ResourceLocation TEXTURE = ImmersivePillagers.locate("textures/entity/undead_evoker.png");

    public UndeadEvokerRenderer(EntityRendererProvider.Context context) {
        super(context, new UndeadIllagerModel<>(context.bakeLayer(UndeadModelLayers.UNDEAD_ILLAGER)), 0.5f);
        this.addLayer(new ItemInHandLayer<UndeadEvoker, IllagerModel<UndeadEvoker>>(this, context.getItemInHandRenderer()) {
            @Override
            public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, UndeadEvoker entity, float f, float g, float h, float j, float k, float l) {
                if (entity.isCastingSpell()) {
                    super.render(poseStack, multiBufferSource, i, entity, f, g, h, j, k, l);
                }
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(UndeadEvoker entity) {
        return TEXTURE;
    }
}
