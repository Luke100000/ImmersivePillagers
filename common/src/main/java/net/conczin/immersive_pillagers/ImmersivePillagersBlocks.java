package net.conczin.immersive_pillagers;

import com.google.common.base.Suppliers;
import net.conczin.immersive_pillagers.block.ReinforcedChestBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public final class ImmersivePillagersBlocks {
    public static final Supplier<Block> REINFORCED_CHEST = Suppliers.memoize(() -> new ReinforcedChestBlock(
            BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.5F, 1_200.0F).sound(SoundType.WOOD)
    ));

    public static void register(BiConsumer<ResourceLocation, Block> registrar) {
        registrar.accept(ImmersivePillagers.locate("reinforced_chest"), REINFORCED_CHEST.get());
    }
}
