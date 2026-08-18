package net.conczin.immersive_pillagers.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.conczin.immersive_pillagers.ImmersivePillagers;
import net.conczin.immersive_pillagers.block.entity.ReinforcedChestBlockEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.chest.ChestModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.phys.Vec3;

public final class ReinforcedChestRenderer implements BlockEntityRenderer<ReinforcedChestBlockEntity, ReinforcedChestRenderer.State> {
    private static final Identifier TEXTURE = ImmersivePillagers.locate("textures/entity/chest/reinforced_chest.png");
    private final ChestModel model;

    public ReinforcedChestRenderer(BlockEntityRendererProvider.Context context) {
        model = new ChestModel(context.bakeLayer(ModelLayers.CHEST));
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(ReinforcedChestBlockEntity chest, State state, float partialTicks, Vec3 cameraPosition,
                                   ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(chest, state, partialTicks, cameraPosition, breakProgress);
        state.facing = chest.getBlockState().getValue(ChestBlock.FACING);
        state.open = chest.getOpenNess(partialTicks);
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.mulPose(ChestRenderer.modelTransformation(state.facing));
        float open = 1.0F - state.open;
        open = 1.0F - open * open * open;
        submitNodeCollector.submitModel(model, open, poseStack, TEXTURE, state.lightCoords,
                OverlayTexture.NO_OVERLAY, 0, state.breakProgress);
        poseStack.popPose();
    }

    public static final class State extends BlockEntityRenderState {
        private Direction facing = Direction.SOUTH;
        private float open;
    }
}
