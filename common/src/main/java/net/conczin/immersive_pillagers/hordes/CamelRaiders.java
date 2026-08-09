package net.conczin.immersive_pillagers.hordes;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.raid.Raider;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;

public class CamelRaiders {
    public static Optional<ActiveHorde> spawn(ServerLevel level, BlockPos pos, @Nullable ServerPlayer target) {
        Camel entity = HordeSpawnUtil.createSaddledCamel(level);
        var spawnPos = HordeSpawnUtil.findGroundSpawn(level, pos, entity);
        if (spawnPos.isEmpty()) {
            return Optional.empty();
        }

        HordeSpawnUtil.placeRandomly(level, entity, spawnPos.get());
        level.addFreshEntity(entity);

        List<Raider> crew = HordeSpawnUtil.addPillagerCrew(level, entity, 2);
        if (target != null) {
            crew.forEach(raider -> raider.setTarget(target));
        }
        HordeSpawnUtil.soundAlarm(entity);

        List<Entity> members = new ArrayList<>();
        members.add(entity);
        members.addAll(crew);
        return Optional.of(new ActiveHorde("camel", Component.translatable("horde.immersive_pillagers.camel_raiders"), level, members));
    }
}
