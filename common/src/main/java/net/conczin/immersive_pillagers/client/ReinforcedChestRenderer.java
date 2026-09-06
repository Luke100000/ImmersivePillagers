package net.conczin.immersive_pillagers.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.conczin.immersive_pillagers.ImmersivePillagers;
import net.conczin.immersive_pillagers.block.entity.ReinforcedChestBlockEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.chest.ChestModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

public class ReinforcedChestRenderer extends ChestRenderer<ReinforcedChestBlockEntity> {
    public static final Identifier TEXTURE = ImmersivePillagers.locate("textures/entity/reinforced_chest.png");
    private final ChestModel model;

    public ReinforcedChestRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
        model = new ChestModel(context.bakeLayer(ModelLayers.CHEST));
    }

    @Override
    public void submit(ChestRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-state.angle));
        poseStack.translate(-0.5F, -0.5F, -0.5F);

        float openness = 1.0F - state.open;
        openness = 1.0F - openness * openness * openness;
        collector.submitModel(model, openness, poseStack, RenderTypes.entityCutout(TEXTURE),
                state.lightCoords, OverlayTexture.NO_OVERLAY, -1, null, 0, state.breakProgress);
        poseStack.popPose();
    }
}
