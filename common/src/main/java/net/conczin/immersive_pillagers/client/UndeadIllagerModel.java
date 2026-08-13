package net.conczin.immersive_pillagers.client;

import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.monster.AbstractIllager;

public class UndeadIllagerModel<T extends AbstractIllager> extends IllagerModel<T> {
    public UndeadIllagerModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        PartDefinition head = partDefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0f, -10.0f, -4.0f, 8.0f, 10.0f, 8.0f), PartPose.offset(0.0f, 0.0f, 0.0f));
        head.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0f, -10.0f, -4.0f, 8.0f, 12.0f, 8.0f, new CubeDeformation(0.45f)), PartPose.ZERO);
        head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0f, -1.0f, -6.0f, 2.0f, 4.0f, 2.0f), PartPose.offset(0.0f, -2.0f, 0.0f));
        partDefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 20).addBox(-4.0f, 0.0f, -3.0f, 8.0f, 12.0f, 6.0f).texOffs(0, 38).addBox(-4.0f, 0.0f, -3.0f, 8.0f, 20.0f, 6.0f, new CubeDeformation(0.5f)), PartPose.offset(0.0f, 0.0f, 0.0f));
        PartDefinition arms = partDefinition.addOrReplaceChild("arms", CubeListBuilder.create().texOffs(44, 22).addBox(-8.0f, -2.0f, -2.0f, 4.0f, 8.0f, 4.0f).texOffs(40, 38).addBox(-4.0f, 2.0f, -2.0f, 8.0f, 4.0f, 4.0f), PartPose.offsetAndRotation(0.0f, 3.0f, -1.0f, -0.75f, 0.0f, 0.0f));
        arms.addOrReplaceChild("left_shoulder", CubeListBuilder.create().texOffs(44, 22).mirror().addBox(4.0f, -2.0f, -2.0f, 4.0f, 8.0f, 4.0f), PartPose.ZERO);
        partDefinition.addOrReplaceChild("right_leg", CubeListBuilder.create()
                        .texOffs(0, 22).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f)
                        .texOffs(46, 24).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 12.0f, 2.0f),
                PartPose.offset(-2.0f, 12.0f, 0.0f));

        partDefinition.addOrReplaceChild("left_leg", CubeListBuilder.create()
                        .texOffs(0, 22).mirror().addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f)
                        .texOffs(46, 24).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 12.0f, 2.0f),
                PartPose.offset(2.0f, 12.0f, 0.0f));

        partDefinition.addOrReplaceChild("right_arm", CubeListBuilder.create()
                        .texOffs(40, 46).addBox(-3.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f)
                        .texOffs(34, 0).addBox(-2.0f, -2.0f, -1.0f, 2.0f, 12.0f, 2.0f),
                PartPose.offset(-5.0f, 2.0f, 0.0f));

        partDefinition.addOrReplaceChild("left_arm", CubeListBuilder.create()
                        .texOffs(40, 46).mirror().addBox(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f)
                        .texOffs(44, 0).addBox(0.0f, -2.0f, -1.0f, 2.0f, 12.0f, 2.0f),
                PartPose.offset(5.0f, 2.0f, 0.0f));

        return LayerDefinition.create(meshDefinition, 64, 64);
    }
}
