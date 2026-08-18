package net.conczin.immersive_pillagers.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.conczin.immersive_pillagers.ImmersivePillagersBlocks;
import net.conczin.immersive_pillagers.block.entity.ReinforcedChestBlockEntity;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityWithoutLevelRenderer.class)
public class BlockEntityWithoutLevelRendererMixin {
    @Shadow
    @Final
    private BlockEntityRenderDispatcher blockEntityRenderDispatcher;
    @Unique
    private ReinforcedChestBlockEntity immersivePillagers$reinforcedChest;

    @Inject(method = "renderByItem", at = @At("HEAD"), cancellable = true)
    private void renderReinforcedChest(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, CallbackInfo callback) {
        if (stack.is(ImmersivePillagersBlocks.REINFORCED_CHEST.get().asItem())) {
            if (immersivePillagers$reinforcedChest == null) {
                immersivePillagers$reinforcedChest = new ReinforcedChestBlockEntity(BlockPos.ZERO, ImmersivePillagersBlocks.REINFORCED_CHEST.get().defaultBlockState());
            }
            blockEntityRenderDispatcher.renderItem(immersivePillagers$reinforcedChest, poseStack, buffer, packedLight, packedOverlay);
            callback.cancel();
        }
    }
}
