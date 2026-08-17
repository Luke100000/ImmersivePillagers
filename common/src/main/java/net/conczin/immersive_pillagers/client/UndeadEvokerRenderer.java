package net.conczin.immersive_pillagers.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.conczin.immersive_pillagers.ImmersivePillagers;
import net.conczin.immersive_pillagers.entity.UndeadEvoker;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.IllagerRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.Identifier;

public class UndeadEvokerRenderer extends IllagerRenderer<UndeadEvoker, UndeadIllagerRenderState> {
    private static final Identifier TEXTURE = ImmersivePillagers.locate("textures/entity/undead_evoker.png");

    public UndeadEvokerRenderer(EntityRendererProvider.Context context) {
        super(context, new UndeadIllagerModel(context.bakeLayer(UndeadModelLayers.UNDEAD_ILLAGER)), 0.5f);
        addLayer(new ItemInHandLayer<>(this) {
            @Override
            public void submit(PoseStack poseStack, SubmitNodeCollector collector, int lightCoords, UndeadIllagerRenderState state, float yRot, float xRot) {
                if (state.isCastingSpell) {
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

    @Override
    public void extractRenderState(UndeadEvoker entity, UndeadIllagerRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.isCastingSpell = entity.isCastingSpell();
    }
}
