package net.conczin.immersive_pillagers.client;

import net.conczin.immersive_pillagers.ImmersivePillagers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.VindicatorRenderer;
import net.minecraft.client.renderer.entity.state.IllagerRenderState;
import net.minecraft.resources.Identifier;

public class UndeadVindicatorRenderer extends VindicatorRenderer {
    private static final Identifier TEXTURE = ImmersivePillagers.locate("textures/entity/undead_vindicator.png");

    public UndeadVindicatorRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new UndeadIllagerModel<>(context.bakeLayer(UndeadModelLayers.UNDEAD_ILLAGER));
    }

    @Override
    public Identifier getTextureLocation(IllagerRenderState state) {
        return TEXTURE;
    }
}
