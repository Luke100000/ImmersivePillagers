package net.conczin.immersive_pillagers.hordes;

import net.conczin.immersive_pillagers.ImmersivePillagers;
import net.conczin.immersive_pillagers.PillagerManager;
import net.conczin.immersive_pillagers.controllers.PillagerCombat;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.illager.Pillager;
import net.minecraft.world.entity.monster.illager.Vindicator;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GroundRaiders {
    private static final double EVOKER_CHANCE = 0.1;
    private static final double VINDICATOR_CHANCE = 0.3;

    public static Optional<ActiveHorde> spawn(ServerLevel level, BlockPos pos, @Nullable ServerPlayer target, int difficulty) {
        List<Entity> members = new ArrayList<>();
        int groupCount = HordeSpawnUtil.getVehicleGroupCount(level, difficulty, 1.0);
        for (int i = 0; i < groupCount; i++) {
            Raider raider = createRaider(level);
            if (raider == null) {
                continue;
            }

            var spawnPos = HordeSpawnUtil.findGroundSpawn(level, pos, raider);
            if (spawnPos.isEmpty()) {
                continue;
            }

            HordeSpawnUtil.placeRandomly(level, raider, spawnPos.get());
            raider.addTag(ImmersivePillagers.MOD_ID);
            HordeSpawnUtil.markTransient(raider);
            level.addFreshEntity(raider);
            if (target != null) {
                raider.setTarget(target);
            }
            members.add(raider);
        }
        if (members.isEmpty()) {
            return Optional.empty();
        }
        HordeSpawnUtil.soundAlarm(level, members.get(0));
        return Optional.of(new ActiveHorde(PillagerManager.HORDE_GROUND, level, members, target));
    }

    @Nullable
    private static Raider createRaider(ServerLevel level) {
        Raider raider;
        double roll = level.getRandom().nextDouble();
        if (roll < EVOKER_CHANCE) {
            raider = EntityType.EVOKER.create(level, EntitySpawnReason.EVENT);
        } else if (roll < EVOKER_CHANCE + VINDICATOR_CHANCE) {
            raider = EntityType.VINDICATOR.create(level, EntitySpawnReason.EVENT);
        } else {
            raider = EntityType.PILLAGER.create(level, EntitySpawnReason.EVENT);
        }

        if (raider instanceof Pillager pillager) {
            PillagerCombat.setCrossbowAttackRange(pillager, 16.0f);
            pillager.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.CROSSBOW));
        } else if (raider instanceof Vindicator vindicator) {
            vindicator.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.IRON_AXE));
        }
        return raider;
    }
}
