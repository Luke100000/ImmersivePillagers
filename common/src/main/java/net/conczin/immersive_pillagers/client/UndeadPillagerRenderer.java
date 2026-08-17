package net.conczin.immersive_pillagers.client;

import net.conczin.immersive_pillagers.ImmersivePillagers;
import net.conczin.immersive_pillagers.entity.UndeadPillager;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.IllagerRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.Identifier;

public class UndeadPillagerRenderer extends IllagerRenderer<UndeadPillager, UndeadIllagerRenderState> {
    private static final Identifier TEXTURE = ImmersivePillagers.locate("textures/entity/undead_pillager.png");

    public UndeadPillagerRenderer(EntityRendererProvider.Context context) {
        super(context, new UndeadIllagerModel(context.bakeLayer(UndeadModelLayers.UNDEAD_ILLAGER)), 0.5f);
        this.addLayer(new ItemInHandLayer<>(this));
    }

    @Override
    public Identifier getTextureLocation(UndeadIllagerRenderState state) {
        return TEXTURE;
    }

    @Override
    public UndeadIllagerRenderState createRenderState() {
        return new UndeadIllagerRenderState();
    }
}
