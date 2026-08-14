package net.conczin.immersive_pillagers.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.conczin.immersive_pillagers.ImmersivePillagers;
import net.conczin.immersive_pillagers.block.entity.ReinforcedChestBlockEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ReinforcedChestRenderer implements BlockEntityRenderer<ReinforcedChestBlockEntity> {
    public static final ResourceLocation TEXTURE = ImmersivePillagers.locate("textures/entity/reinforced_chest.png");
    private final ModelPart lid;
    private final ModelPart bottom;
    private final ModelPart lock;

    public ReinforcedChestRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart model = context.bakeLayer(ModelLayers.CHEST);
        bottom = model.getChild("bottom");
        lid = model.getChild("lid");
        lock = model.getChild("lock");
    }

    @Override
    public void render(ReinforcedChestBlockEntity chest, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Level level = chest.getLevel();
        BlockState state = level != null ? chest.getBlockState() : Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.SOUTH);

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-state.getValue(ChestBlock.FACING).toYRot()));
        poseStack.translate(-0.5F, -0.5F, -0.5F);

        float openness = 1.0F - chest.getOpenNess(partialTick);
        openness = 1.0F - openness * openness * openness;
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutout(TEXTURE));
        lid.xRot = -(openness * ((float) Math.PI / 2F));
        lock.xRot = lid.xRot;
        lid.render(poseStack, consumer, packedLight, packedOverlay);
        lock.render(poseStack, consumer, packedLight, packedOverlay);
        bottom.render(poseStack, consumer, packedLight, packedOverlay);
        poseStack.popPose();
    }
}
