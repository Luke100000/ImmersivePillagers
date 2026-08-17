package net.conczin.immersive_pillagers.item;

import net.conczin.immersive_pillagers.PillagerManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class RaidersHornItem extends TooltippedItem {
    public RaidersHornItem(Properties properties) {
        super(properties, "item.immersive_pillagers.raiders_horn.tooltip");
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            if (PillagerManager.hasActiveHordeNearby(serverPlayer.serverLevel(), serverPlayer.blockPosition())
                || !PillagerManager.summonWarHorde(serverPlayer)) {
                return InteractionResultHolder.fail(stack);
            }

            stack.hurtAndBreak(1, serverPlayer, holder -> holder.broadcastBreakEvent(hand));
            serverPlayer.getCooldowns().addCooldown(this, 100);
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }
}
