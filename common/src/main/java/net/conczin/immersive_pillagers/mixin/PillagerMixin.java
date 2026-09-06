package net.conczin.immersive_pillagers.mixin;

import net.conczin.immersive_pillagers.access.PillagerRangeAccessor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.RangedCrossbowAttackGoal;
import net.minecraft.world.entity.monster.illager.AbstractIllager;
import net.minecraft.world.entity.monster.illager.Pillager;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Pillager.class)
public abstract class PillagerMixin extends AbstractIllager implements PillagerRangeAccessor {
    @Unique
    private RangedCrossbowAttackGoal<Pillager> immersivePillagers$crossbowAttackGoal;

    protected PillagerMixin(EntityType<? extends AbstractIllager> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void immersivePillagers$setCrossbowAttackRange(float range) {
        if (this.immersivePillagers$crossbowAttackGoal != null) {
            this.goalSelector.removeGoal(this.immersivePillagers$crossbowAttackGoal);
        }

        Pillager pillager = (Pillager) (Object) this;
        this.immersivePillagers$crossbowAttackGoal = new RangedCrossbowAttackGoal<>(pillager, 1.0, range);
        this.goalSelector.addGoal(3, this.immersivePillagers$crossbowAttackGoal);
    }
}
