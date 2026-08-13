package net.conczin.immersive_pillagers.client;

import net.conczin.immersive_pillagers.ImmersivePillagers;
import net.conczin.immersive_pillagers.entity.UndeadPillager;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.PillagerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Pillager;

public class UndeadPillagerRenderer extends PillagerRenderer {
    private static final ResourceLocation TEXTURE = ImmersivePillagers.locate("textures/entity/undead_pillager.png");

    public UndeadPillagerRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(Pillager entity) {
        return TEXTURE;
    }
}
