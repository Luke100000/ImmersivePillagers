package net.conczin.immersive_pillagers.item;

import net.conczin.immersive_pillagers.PillagerManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
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
            if (!PillagerManager.summonWarHorde(serverPlayer)) {
                return InteractionResultHolder.fail(stack);
            }
            stack.hurtAndBreak(1, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
