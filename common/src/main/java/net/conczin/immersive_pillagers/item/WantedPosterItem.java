package net.conczin.immersive_pillagers.item;

import net.conczin.immersive_pillagers.PillagerManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class WantedPosterItem extends TooltippedItem {
    public WantedPosterItem(Properties properties) {
        super(properties, "item.immersive_pillagers.wanted_poster.tooltip");
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            PillagerManager.openWantedPoster(serverPlayer, hand);
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }
}
