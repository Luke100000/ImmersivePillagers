package net.conczin.immersive_pillagers.controllers;

import net.conczin.immersive_pillagers.ImmersivePillagers;
import net.conczin.immersive_pillagers.PillagerManager;
import net.minecraft.world.entity.monster.illager.Pillager;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.entity.raid.Raider;

public class SpiderController {
    private static final double MAX_SPEED_MODIFIER = 1.5;
    private static final double STOP_DISTANCE = 1.0;
    private static final double PILLAGER_RADIUS = 8.0;
    private static final double ORBIT_SPEED = 0.01;

    public static void tick(Spider spider) {
        if (spider.level().isClientSide() || !spider.entityTags().contains(ImmersivePillagers.HORDE_ENTITY_TAG) || !(spider.getControllingPassenger() instanceof Raider rider)) {
            return;
        }

        PillagerManager.getClosestPlayer(rider).ifPresent(target -> {
            rider.setTarget(target);
            if (rider instanceof Pillager) {
                HordeNavigation.orbit(spider, target, PILLAGER_RADIUS, ORBIT_SPEED, MAX_SPEED_MODIFIER, STOP_DISTANCE, 1.0);
            } else {
                HordeNavigation.moveTo(spider, target.position(), MAX_SPEED_MODIFIER, STOP_DISTANCE, 1.0);
            }
        });
    }
}
