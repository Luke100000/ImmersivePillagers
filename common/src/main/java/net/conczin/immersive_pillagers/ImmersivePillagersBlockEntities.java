package net.conczin.immersive_pillagers;

import net.minecraft.core.BlockPos;
import net.conczin.immersive_pillagers.block.entity.ReinforcedChestBlockEntity;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.BiConsumer;

public final class ImmersivePillagersBlockEntities {
    public static BlockEntityType<ReinforcedChestBlockEntity> REINFORCED_CHEST;

    public static void register(BiConsumer<Identifier, BlockEntityType<?>> registrar, BlockEntityTypeBuilder<ReinforcedChestBlockEntity> builder) {
        REINFORCED_CHEST = register(registrar, ImmersivePillagers.locate("reinforced_chest"), ReinforcedChestBlockEntity::new, builder, ImmersivePillagersBlocks.REINFORCED_CHEST.get());
    }

    private static <T extends BlockEntity> BlockEntityType<T> register(BiConsumer<Identifier, BlockEntityType<?>> registrar, Identifier id, BlockEntityFactory<T> factory, BlockEntityTypeBuilder<T> builder, Block... blocks) {
        BlockEntityType<T> type = builder.build(factory, blocks);
        registrar.accept(id, type);
        return type;
    }

    @FunctionalInterface
    public interface BlockEntityFactory<T extends BlockEntity> {
        T create(BlockPos pos, BlockState state);
    }

    @FunctionalInterface
    public interface BlockEntityTypeBuilder<T extends BlockEntity> {
        BlockEntityType<T> build(BlockEntityFactory<T> factory, Block... blocks);
    }
}
