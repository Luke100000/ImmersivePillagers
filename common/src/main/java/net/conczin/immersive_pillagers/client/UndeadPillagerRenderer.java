package net.conczin.immersive_pillagers.client;

import net.conczin.immersive_pillagers.ImmersivePillagers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.PillagerRenderer;
import net.minecraft.client.renderer.entity.state.IllagerRenderState;
import net.minecraft.resources.Identifier;

public class UndeadPillagerRenderer extends PillagerRenderer {
    private static final Identifier TEXTURE = ImmersivePillagers.locate("textures/entity/undead_pillager.png");

    public UndeadPillagerRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new UndeadIllagerModel<>(context.bakeLayer(UndeadModelLayers.UNDEAD_ILLAGER));
    }

    @Override
    public Identifier getTextureLocation(IllagerRenderState state) {
        return TEXTURE;
    }
}
