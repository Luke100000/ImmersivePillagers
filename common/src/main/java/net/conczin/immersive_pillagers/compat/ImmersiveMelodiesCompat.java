package net.conczin.immersive_pillagers.compat;

import net.conczin.immersive_pillagers.ImmersivePillagers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ImmersiveMelodiesCompat {
    public static final String TAG_PLAYING = "playing";
    public static final String TAG_MELODY = "melody";
    public static final String TAG_START_TIME = "start_time";
    public static final String TAG_TRACKS = "enabled_tracks";

    private static final Identifier DEFAULT_MELODY = ImmersivePillagers.locate("melodies/ride_of_the_valkyries.mid");
    private static final Map<String, Identifier> HORDE_MELODIES = Map.of(
            "gyrodyne", DEFAULT_MELODY,
            "camel", ImmersivePillagers.locate("melodies/arabe_yaabud.mid"),
            "boat", ImmersivePillagers.locate("melodies/drunken_sailor.mid"),
            "horse", ImmersivePillagers.locate("melodies/william_tell_overture.mid"),
            "spider", ImmersivePillagers.locate("melodies/in_the_hall_of_the_mountain_king.mid")
    );

    public static void playTrack(Level level, ItemStack stack) {
        playTrack(level, stack, DEFAULT_MELODY);
    }

    public static void playTrack(Level level, ItemStack stack, String hordeType) {
        playTrack(level, stack, HORDE_MELODIES.getOrDefault(hordeType, DEFAULT_MELODY));
    }

    public static void playTrack(Level level, ItemStack stack, Identifier name) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            tag.putString(TAG_MELODY, name.toString());
            tag.putBoolean(TAG_PLAYING, true);
            tag.putLong(TAG_START_TIME, level.getGameTime());
            tag.remove(TAG_TRACKS);
        });
    }

    public static List<Identifier> INSTRUMENTS = List.of(
            Identifier.fromNamespaceAndPath("immersive_melodies", "trumpet"),
            Identifier.fromNamespaceAndPath("immersive_melodies", "lute")
    );

    public static Optional<ItemStack> getInstrument(Level level) {
        Identifier instrument = INSTRUMENTS.get(level.random.nextInt(INSTRUMENTS.size()));
        Item item = BuiltInRegistries.ITEM.getValue(instrument);
        if (item instanceof AirItem) {
            return Optional.empty();
        }
        return Optional.of(new ItemStack(item));
    }
}
