package net.conczin.immersive_pillagers;

import com.google.common.base.Suppliers;
import net.conczin.immersive_pillagers.block.ReinforcedChestBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public final class ImmersivePillagersBlocks {
    private static final ResourceKey<Block> REINFORCED_CHEST_KEY = ResourceKey.create(Registries.BLOCK, ImmersivePillagers.locate("reinforced_chest"));
    public static final Supplier<Block> REINFORCED_CHEST = Suppliers.memoize(() -> new ReinforcedChestBlock(
            BlockBehaviour.Properties.of().setId(REINFORCED_CHEST_KEY).mapColor(MapColor.WOOD).strength(2.5F, 1_200.0F).sound(SoundType.WOOD)
    ));

    public static void register(BiConsumer<Identifier, Block> registrar) {
        registrar.accept(ImmersivePillagers.locate("reinforced_chest"), REINFORCED_CHEST.get());
    }
}
