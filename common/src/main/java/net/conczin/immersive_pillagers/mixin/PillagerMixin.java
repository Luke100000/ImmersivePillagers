package net.conczin.immersive_pillagers.mixin;

import net.conczin.immersive_pillagers.ImmersivePillagers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.RangedCrossbowAttackGoal;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Pillager.class)
public abstract class PillagerMixin extends AbstractIllager {
    protected PillagerMixin(EntityType<? extends AbstractIllager> entityType, Level level) {
        super(entityType, level);
    }

    /**
     * Give pillagers on vehicles more range.
     */
    @Inject(method = "registerGoals", at = @At("HEAD"))
    private void registerGoals(CallbackInfo ci) {
        Pillager pillager = (Pillager) (Object) this;
        if (pillager.getVehicle() != null && pillager.getTags().contains(ImmersivePillagers.MOD_ID)) {
            this.goalSelector.addGoal(3, new RangedCrossbowAttackGoal<>(pillager, 1.0, 16.0f));
        }
    }
}
