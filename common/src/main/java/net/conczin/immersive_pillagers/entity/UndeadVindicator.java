package net.conczin.immersive_pillagers.entity;

import net.conczin.immersive_pillagers.ImmersivePillagersSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.illager.Vindicator;
import net.minecraft.world.level.Level;

public class UndeadVindicator extends Vindicator {
    public UndeadVindicator(EntityType<? extends Vindicator> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public boolean isInvertedHealAndHarm() {
        return true;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.28D)
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ImmersivePillagersSounds.UNDEAD_VINDICATOR_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ImmersivePillagersSounds.UNDEAD_VINDICATOR_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ImmersivePillagersSounds.UNDEAD_VINDICATOR_DEATH;
    }
}
