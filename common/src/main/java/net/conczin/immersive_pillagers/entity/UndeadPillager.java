package net.conczin.immersive_pillagers.entity;

import net.conczin.immersive_pillagers.ImmersivePillagersSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.level.Level;

public class UndeadPillager extends Pillager {
    public UndeadPillager(EntityType<? extends Pillager> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ImmersivePillagersSounds.UNDEAD_PILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ImmersivePillagersSounds.UNDEAD_PILLAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ImmersivePillagersSounds.UNDEAD_PILLAGER_DEATH;
    }
}
