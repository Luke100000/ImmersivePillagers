package net.conczin.immersive_pillagers;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.function.BiConsumer;

public class ImmersivePillagersSounds {
    public static final SoundEvent UNDEAD_PILLAGER_AMBIENT = SoundEvent.createVariableRangeEvent(ImmersivePillagers.locate("entity.undead_pillager.ambient"));
    public static final SoundEvent UNDEAD_PILLAGER_HURT = SoundEvent.createVariableRangeEvent(ImmersivePillagers.locate("entity.undead_pillager.hurt"));
    public static final SoundEvent UNDEAD_PILLAGER_DEATH = SoundEvent.createVariableRangeEvent(ImmersivePillagers.locate("entity.undead_pillager.death"));

    public static final SoundEvent UNDEAD_EVOKER_AMBIENT = SoundEvent.createVariableRangeEvent(ImmersivePillagers.locate("entity.undead_evoker.ambient"));
    public static final SoundEvent UNDEAD_EVOKER_HURT = SoundEvent.createVariableRangeEvent(ImmersivePillagers.locate("entity.undead_evoker.hurt"));
    public static final SoundEvent UNDEAD_EVOKER_DEATH = SoundEvent.createVariableRangeEvent(ImmersivePillagers.locate("entity.undead_evoker.death"));

    public static final SoundEvent UNDEAD_VINDICATOR_AMBIENT = SoundEvent.createVariableRangeEvent(ImmersivePillagers.locate("entity.undead_vindicator.ambient"));
    public static final SoundEvent UNDEAD_VINDICATOR_HURT = SoundEvent.createVariableRangeEvent(ImmersivePillagers.locate("entity.undead_vindicator.hurt"));
    public static final SoundEvent UNDEAD_VINDICATOR_DEATH = SoundEvent.createVariableRangeEvent(ImmersivePillagers.locate("entity.undead_vindicator.death"));

    public static final SoundEvent REINFORCED_CHEST_UNLOCK = SoundEvent.createVariableRangeEvent(ImmersivePillagers.locate("block.reinforced_chest.unlock"));

    public static void register(BiConsumer<ResourceLocation, SoundEvent> registrar) {
        registrar.accept(ImmersivePillagers.locate("entity.undead_pillager.ambient"), UNDEAD_PILLAGER_AMBIENT);
        registrar.accept(ImmersivePillagers.locate("entity.undead_pillager.hurt"), UNDEAD_PILLAGER_HURT);
        registrar.accept(ImmersivePillagers.locate("entity.undead_pillager.death"), UNDEAD_PILLAGER_DEATH);

        registrar.accept(ImmersivePillagers.locate("entity.undead_evoker.ambient"), UNDEAD_EVOKER_AMBIENT);
        registrar.accept(ImmersivePillagers.locate("entity.undead_evoker.hurt"), UNDEAD_EVOKER_HURT);
        registrar.accept(ImmersivePillagers.locate("entity.undead_evoker.death"), UNDEAD_EVOKER_DEATH);

        registrar.accept(ImmersivePillagers.locate("entity.undead_vindicator.ambient"), UNDEAD_VINDICATOR_AMBIENT);
        registrar.accept(ImmersivePillagers.locate("entity.undead_vindicator.hurt"), UNDEAD_VINDICATOR_HURT);
        registrar.accept(ImmersivePillagers.locate("entity.undead_vindicator.death"), UNDEAD_VINDICATOR_DEATH);

        registrar.accept(ImmersivePillagers.locate("block.reinforced_chest.unlock"), REINFORCED_CHEST_UNLOCK);
    }
}
