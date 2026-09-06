package net.conczin.immersive_pillagers.entity;

import net.conczin.immersive_pillagers.ImmersivePillagersSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.illager.Evoker;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class UndeadEvoker extends Evoker {
    public UndeadEvoker(EntityType<? extends Evoker> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public boolean isInvertedHealAndHarm() {
        return true;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.FOLLOW_RANGE, 12.0D);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ImmersivePillagersSounds.UNDEAD_EVOKER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ImmersivePillagersSounds.UNDEAD_EVOKER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ImmersivePillagersSounds.UNDEAD_EVOKER_DEATH;
    }
}
