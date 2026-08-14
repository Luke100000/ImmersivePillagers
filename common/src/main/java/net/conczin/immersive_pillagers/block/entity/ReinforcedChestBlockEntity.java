package net.conczin.immersive_pillagers.block.entity;

import net.conczin.immersive_pillagers.ImmersivePillagersBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ReinforcedChestBlockEntity extends ChestBlockEntity {
    public ReinforcedChestBlockEntity(BlockPos pos, BlockState state) {
        super(ImmersivePillagersBlockEntities.REINFORCED_CHEST, pos, state);
    }

    @Override
    protected void signalOpenCount(Level level, BlockPos pos, BlockState state, int oldOpenCount, int openCount) {
        super.signalOpenCount(level, pos, state, oldOpenCount, openCount);
        if (oldOpenCount != openCount) {
            level.updateNeighborsAt(pos, state.getBlock());
            level.updateNeighborsAt(pos.below(), state.getBlock());
        }
    }
}
