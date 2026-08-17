package net.conczin.immersive_pillagers.mixin;

import net.conczin.immersive_pillagers.controllers.BoatController;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.entity.vehicle.boat.Boat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractBoat.class)
public class BoatControllerMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void immersivePillagers$tickController(CallbackInfo ci) {
        if ((Object) this instanceof Boat boat) {
            BoatController.tick(boat);
        }
    }
}
