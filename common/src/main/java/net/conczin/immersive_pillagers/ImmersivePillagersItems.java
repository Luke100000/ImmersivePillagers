package net.conczin.immersive_pillagers;

import com.google.common.base.Suppliers;
import net.conczin.immersive_pillagers.item.RaidersHornItem;
import net.conczin.immersive_pillagers.item.ResearchNoteItem;
import net.conczin.immersive_pillagers.item.TooltippedItem;
import net.conczin.immersive_pillagers.item.WantedPosterItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class ImmersivePillagersItems {
    public static final ResourceKey<CreativeModeTab> CREATIVE_TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, ImmersivePillagers.locate("main"));
    public static final Supplier<Item> RUSTY_KEY = Suppliers.memoize(() -> new TooltippedItem(properties("rusty_key"), "item.immersive_pillagers.rusty_key.tooltip"));
    public static final Supplier<Item> WANTED_POSTER = Suppliers.memoize(() -> new WantedPosterItem(properties("wanted_poster")));
    public static final Supplier<Item> RESEARCH_NOTE = Suppliers.memoize(() -> new ResearchNoteItem(properties("research_note").stacksTo(1)));
    public static final Supplier<Item> CRUDE_TOTEM_OF_UNDYING = Suppliers.memoize(() -> new TooltippedItem(properties("crude_totem_of_undying").stacksTo(1), "item.immersive_pillagers.crude_totem_of_undying.tooltip"));
    public static final Supplier<Item> RAIDERS_HORN = Suppliers.memoize(() -> new RaidersHornItem(properties("raiders_horn").stacksTo(1).durability(3)));
    public static final Supplier<Item> REINFORCED_CHEST = Suppliers.memoize(() -> new BlockItem(ImmersivePillagersBlocks.REINFORCED_CHEST.get(), properties("reinforced_chest")));

    public static final Supplier<CreativeModeTab> CREATIVE_TAB = Suppliers.memoize(() -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .title(Component.translatable("itemGroup.immersive_pillagers"))
            .icon(() -> new ItemStack(RAIDERS_HORN.get()))
            .build());

    public static void register(BiConsumer<Identifier, Item> registrar) {
        registrar.accept(ImmersivePillagers.locate("rusty_key"), RUSTY_KEY.get());
        registrar.accept(ImmersivePillagers.locate("wanted_poster"), WANTED_POSTER.get());
        registrar.accept(ImmersivePillagers.locate("research_note"), RESEARCH_NOTE.get());
        registrar.accept(ImmersivePillagers.locate("crude_totem_of_undying"), CRUDE_TOTEM_OF_UNDYING.get());
        registrar.accept(ImmersivePillagers.locate("raiders_horn"), RAIDERS_HORN.get());
        registrar.accept(ImmersivePillagers.locate("reinforced_chest"), REINFORCED_CHEST.get());
    }

    public static void addCreativeTabItems(Consumer<Item> output) {
        output.accept(RUSTY_KEY.get());
        output.accept(WANTED_POSTER.get());
        output.accept(RESEARCH_NOTE.get());
        output.accept(CRUDE_TOTEM_OF_UNDYING.get());
        output.accept(RAIDERS_HORN.get());
        output.accept(REINFORCED_CHEST.get());
    }

    private static Item.Properties properties(String name) {
        return new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ImmersivePillagers.locate(name)));
    }
}
