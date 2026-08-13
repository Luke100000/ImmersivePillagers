package net.conczin.immersive_pillagers.client;

import net.conczin.immersive_pillagers.ImmersivePillagers;
import net.conczin.immersive_pillagers.entity.UndeadEvoker;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EvokerRenderer;
import net.minecraft.resources.ResourceLocation;

public class UndeadEvokerRenderer extends EvokerRenderer<UndeadEvoker> {
    private static final ResourceLocation TEXTURE = ImmersivePillagers.locate("textures/entity/undead_evoker.png");

    public UndeadEvokerRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(UndeadEvoker entity) {
        return TEXTURE;
    }
}
