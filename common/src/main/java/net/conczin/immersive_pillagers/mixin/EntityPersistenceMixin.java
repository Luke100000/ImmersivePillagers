package net.conczin.immersive_pillagers.mixin;

import net.conczin.immersive_pillagers.ImmersivePillagers;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityPersistenceMixin {
    @Inject(method = "shouldBeSaved", at = @At("HEAD"), cancellable = true)
    private void immersivePillagers$preventHordeEntitySaving(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity.entityTags().contains(ImmersivePillagers.HORDE_ENTITY_TAG)) {
            cir.setReturnValue(false);
        }
    }
}
