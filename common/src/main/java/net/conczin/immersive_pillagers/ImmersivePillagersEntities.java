package net.conczin.immersive_pillagers;

import com.google.common.base.Suppliers;
import net.conczin.immersive_pillagers.entity.UndeadEvoker;
import net.conczin.immersive_pillagers.entity.UndeadPillager;
import net.conczin.immersive_pillagers.entity.UndeadVindicator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ImmersivePillagersEntities {
    public static final Supplier<EntityType<UndeadPillager>> UNDEAD_PILLAGER = Suppliers.memoize(() ->
            EntityType.Builder.of(UndeadPillager::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8)
                    .build("undead_pillager"));

    public static final Supplier<EntityType<UndeadEvoker>> UNDEAD_EVOKER = Suppliers.memoize(() ->
            EntityType.Builder.of(UndeadEvoker::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8)
                    .build("undead_evoker"));

    public static final Supplier<EntityType<UndeadVindicator>> UNDEAD_VINDICATOR = Suppliers.memoize(() ->
            EntityType.Builder.of(UndeadVindicator::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8)
                    .build("undead_vindicator"));

    public static void register(BiConsumer<ResourceLocation, EntityType<?>> registrar) {
        registrar.accept(ImmersivePillagers.locate("undead_pillager"), UNDEAD_PILLAGER.get());
        registrar.accept(ImmersivePillagers.locate("undead_evoker"), UNDEAD_EVOKER.get());
        registrar.accept(ImmersivePillagers.locate("undead_vindicator"), UNDEAD_VINDICATOR.get());
    }
}
