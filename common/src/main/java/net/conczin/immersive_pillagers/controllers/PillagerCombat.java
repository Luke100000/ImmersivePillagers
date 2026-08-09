package net.conczin.immersive_pillagers.controllers;

import net.conczin.immersive_pillagers.mixin.PillagerRangeAccessor;
import net.minecraft.world.entity.monster.Pillager;

public class PillagerCombat {
    public static void setCrossbowAttackRange(Pillager pillager, float range) {
        ((PillagerRangeAccessor) pillager).immersivePillagers$setCrossbowAttackRange(range);
    }
}
