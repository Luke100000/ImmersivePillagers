package net.conczin.immersive_pillagers.client;

import net.conczin.immersive_pillagers.ImmersivePillagers;
import net.conczin.immersive_pillagers.entity.UndeadVindicator;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.model.monster.illager.IllagerModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.IllagerRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.Identifier;

public class UndeadVindicatorRenderer extends IllagerRenderer<UndeadVindicator, UndeadIllagerRenderState> {
    private static final Identifier TEXTURE = ImmersivePillagers.locate("textures/entity/undead_vindicator.png");

    public UndeadVindicatorRenderer(EntityRendererProvider.Context context) {
        super(context, new UndeadIllagerModel(context.bakeLayer(UndeadModelLayers.UNDEAD_ILLAGER)), 0.5f);
        addLayer(new ItemInHandLayer<UndeadIllagerRenderState, IllagerModel<UndeadIllagerRenderState>>(this) {
            @Override
            public void submit(PoseStack poseStack, SubmitNodeCollector collector, int lightCoords, UndeadIllagerRenderState state, float yRot, float xRot) {
                if (state.isAggressive) {
                    super.submit(poseStack, collector, lightCoords, state, yRot, xRot);
                }
            }
        });
    }

    @Override
    public Identifier getTextureLocation(UndeadIllagerRenderState state) {
        return TEXTURE;
    }

    @Override
    public UndeadIllagerRenderState createRenderState() {
        return new UndeadIllagerRenderState();
    }
}
