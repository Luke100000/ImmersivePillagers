package net.conczin.immersive_pillagers.client;

import net.conczin.immersive_pillagers.ImmersivePillagers;
import net.conczin.immersive_pillagers.entity.UndeadEvoker;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EvokerRenderer;
import net.minecraft.client.renderer.entity.state.EvokerRenderState;
import net.minecraft.resources.Identifier;

public class UndeadEvokerRenderer extends EvokerRenderer<UndeadEvoker> {
    private static final Identifier TEXTURE = ImmersivePillagers.locate("textures/entity/undead_evoker.png");

    public UndeadEvokerRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new UndeadIllagerModel<>(context.bakeLayer(UndeadModelLayers.UNDEAD_ILLAGER));
    }

    @Override
    public Identifier getTextureLocation(EvokerRenderState state) {
        return TEXTURE;
    }
}
