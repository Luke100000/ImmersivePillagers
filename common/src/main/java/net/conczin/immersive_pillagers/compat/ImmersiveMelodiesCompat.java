package net.conczin.immersive_pillagers.compat;

import net.conczin.immersive_pillagers.ImmersivePillagers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

public class ImmersiveMelodiesCompat {
    public static final String TAG_PLAYING = "playing";
    public static final String TAG_MELODY = "melody";
    public static final String TAG_START_TIME = "start_time";
    public static final String TAG_TRACKS = "enabled_tracks";

    public static void playTrack(Level level, ItemStack stack) {
        playTrack(level, stack, ImmersivePillagers.locate("melodies/ride_of_the_valkyries"));
    }

    public static void playTrack(Level level, ItemStack stack, ResourceLocation name) {
        stack.getOrCreateTag().putString(TAG_MELODY, name.toString());
        stack.getOrCreateTag().putBoolean(TAG_PLAYING, true);
        stack.getOrCreateTag().putLong(TAG_START_TIME, level.getGameTime());
        stack.getOrCreateTag().remove(TAG_TRACKS);
    }

    public static List<ResourceLocation> INSTRUMENTS = List.of(
            new ResourceLocation("immersive_melodies", "trumpet"),
            new ResourceLocation("immersive_melodies", "lute")
    );

    public static Optional<ItemStack> getInstrument(Level level) {
        ResourceLocation instrument = INSTRUMENTS.get(level.random.nextInt(INSTRUMENTS.size()));
        Item item = BuiltInRegistries.ITEM.get(instrument);
        if (item instanceof AirItem) {
            return Optional.empty();
        }
        return Optional.of(new ItemStack(item));
    }
}
