package net.conczin.immersive_pillagers.mixin;

import net.conczin.immersive_pillagers.controllers.SpiderController;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Spider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public class SpiderControllerMixin {
    @Inject(method = "customServerAiStep", at = @At("TAIL"))
    private void immersivePillagers$tickSpiderController(CallbackInfo ci) {
        if ((Object) this instanceof Spider spider) {
            SpiderController.tick(spider);
        }
    }
}
