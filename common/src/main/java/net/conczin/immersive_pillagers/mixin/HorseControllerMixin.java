package net.conczin.immersive_pillagers.mixin;

import net.conczin.immersive_pillagers.controllers.HorseController;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public class HorseControllerMixin {
    @Inject(method = "customServerAiStep", at = @At("TAIL"))
    private void immersivePillagers$tickHorseController(CallbackInfo ci) {
        if ((Object) this instanceof AbstractHorse horse) {
            HorseController.tick(horse);
        }
    }
}
