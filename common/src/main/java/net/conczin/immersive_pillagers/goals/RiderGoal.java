package net.conczin.immersive_pillagers.goals;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.monster.illager.Pillager;

import java.util.EnumSet;

public class RiderGoal extends Goal {
    private final AbstractHorse horse;
    private final double speedModifier;
    private final double minDist;
    private final double maxDist;
    private double posX;
    private double posY;
    private double posZ;

    public RiderGoal(AbstractHorse horse, double speedModifier, double minDist, double maxDist) {
        this.horse = horse;
        this.speedModifier = speedModifier;
        this.minDist = minDist * minDist;
        this.maxDist = maxDist * maxDist;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.horse.getControllingPassenger() instanceof Pillager pillager && pillager.getTarget() != null) {
            LivingEntity target = pillager.getTarget();
            double dist = target.distanceToSqr(horse);
            double dx = target.getX() - horse.getX();
            double dy = target.getY() - horse.getY();
            double dz = target.getZ() - horse.getZ();
            if (dist > maxDist|| dist < minDist) {
                this.posX = target.getX() + dx * 0.5;
                this.posY = target.getY() + dy * 0.5;
                this.posZ = target.getZ() + dz * 0.5;
                return true;
            }
        }
        return false;
    }

    @Override
    public void start() {
        this.horse.getNavigation().moveTo(this.posX, this.posY, this.posZ, this.speedModifier);
    }

    @Override
    public boolean canContinueToUse() {
        return this.horse.getControllingPassenger() instanceof Pillager pillager && pillager.getTarget() != null && !this.horse.getNavigation().isDone();
    }
}
