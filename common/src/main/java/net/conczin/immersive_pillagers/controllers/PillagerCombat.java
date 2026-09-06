package net.conczin.immersive_pillagers.controllers;

import net.conczin.immersive_pillagers.access.PillagerRangeAccessor;
import net.minecraft.world.entity.monster.illager.Pillager;

public class PillagerCombat {
    public static void setCrossbowAttackRange(Pillager pillager, float range) {
        ((PillagerRangeAccessor) pillager).immersivePillagers$setCrossbowAttackRange(range);
    }
}
