package net.conczin.immersive_pillagers.mixin;

import immersive_aircraft.entity.VehicleEntity;
import net.conczin.immersive_pillagers.controllers.AircraftController;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Pillager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "immersive_aircraft.entity.VehicleEntity")
public class VehicleEntityMixin {
    @Inject(method = "tickPilot()V", at = @At("TAIL"), remap = false)
    private void immersivePillagers$tickPilot(CallbackInfo ci) {
        AircraftController.tickPilot((VehicleEntity) (Object) this);
    }

    @Inject(method = "canTurnOnEngine(Lnet/minecraft/world/entity/Entity;)Z", at = @At("HEAD"), cancellable = true)
    private void immersivePillagers$canTurnOnEngine(Entity pilot, CallbackInfoReturnable<Boolean> cir) {
        if (((Entity) (Object) this).getControllingPassenger() instanceof Pillager) {
            cir.setReturnValue(true);
        }
    }
}
