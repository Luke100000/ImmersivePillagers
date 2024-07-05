package net.conczin.immersive_pillagers.mixin;

import immersive_aircraft.entity.VehicleEntity;
import net.conczin.immersive_pillagers.PillagerManager;
import net.conczin.immersive_pillagers.controllers.AircraftController;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VehicleEntity.class)
public class VehicleEntityMixin {
    @Inject(method = "tickPilot()V", at = @At("TAIL"), remap = false)
    private void immersivePillagers$tickPilot(CallbackInfo ci) {
        AircraftController.tickPilot((VehicleEntity) (Object) this);
    }

    @Inject(method = "canTurnOnEngine(Lnet/minecraft/world/entity/Entity;)Z", at = @At("HEAD"), cancellable = true)
    private void immersivePillagers$canTurnOnEngine(Entity pilot, CallbackInfoReturnable<Boolean> cir) {
        if (PillagerManager.canTurnOnEngine((VehicleEntity) (Object) this)) {
            cir.setReturnValue(true);
        }
    }
}
