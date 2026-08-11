package net.conczin.immersive_pillagers;

import com.google.common.base.Suppliers;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public final class ImmersivePillagersItems {
    public static final Supplier<Item> RUSTY_KEY = item();
    public static final Supplier<Item> WANTED_POSTER = item();
    public static final Supplier<Item> RAIDERS_HORN = item();

    public static final Supplier<CreativeModeTab> CREATIVE_TAB = Suppliers.memoize(() -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .title(Component.translatable("itemGroup.immersive_pillagers"))
            .icon(() -> new ItemStack(RAIDERS_HORN.get()))
            .displayItems((parameters, output) -> {
                output.accept(RUSTY_KEY.get());
                output.accept(WANTED_POSTER.get());
                output.accept(RAIDERS_HORN.get());
            })
            .build());

    public static void register(BiConsumer<ResourceLocation, Item> registrar) {
        registrar.accept(ImmersivePillagers.locate("rusty_key"), RUSTY_KEY.get());
        registrar.accept(ImmersivePillagers.locate("wanted_poster"), WANTED_POSTER.get());
        registrar.accept(ImmersivePillagers.locate("raiders_horn"), RAIDERS_HORN.get());
    }

    private static Supplier<Item> item() {
        return Suppliers.memoize(() -> new Item(new Item.Properties()));
    }
}
