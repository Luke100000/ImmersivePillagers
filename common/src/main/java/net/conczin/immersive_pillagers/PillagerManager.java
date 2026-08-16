package net.conczin.immersive_pillagers;

import net.conczin.immersive_pillagers.config.Config;
import net.conczin.immersive_pillagers.entity.UndeadPillager;
import net.conczin.immersive_pillagers.hordes.*;
import net.conczin.immersive_pillagers.network.Handler;
import net.conczin.immersive_pillagers.network.packet.OpenWantedPosterPacket;
import net.conczin.immersive_pillagers.network.packet.WantedPosterActionPacket;
import net.conczin.immersive_pillagers.player.PlayerHordeData;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PillagerManager {
    private static final Map<String, HordeSpawner> HORDES = new HashMap<>();
    private static final Map<UUID, ActiveHorde> ACTIVE_HORDES = new HashMap<>();

    private static final ResourceLocation CRUDE_TOTEM_AWAKENED_ADVANCEMENT = ImmersivePillagers.locate("research/crude_totem_awakened");
    private static final ResourceLocation HORDE_CONQUEROR_ADVANCEMENT = ImmersivePillagers.locate("horde_conqueror");

    public static String registerHorde(String name, HordeSpawner horde) {
        HORDES.put(name, horde);
        return name;
    }

    public static final String HORDE_GYRODYNE = "gyrodyne";
    public static final String HORDE_CAMEL = registerHorde("camel", CamelRaiders::spawn);
    public static final String HORDE_BOAT = registerHorde("boat", BoatRaiders::spawn);
    public static final String HORDE_HORSE = registerHorde("horse", HorseRaiders::spawn);
    public static final String HORDE_SPIDER = registerHorde("spider", SpiderRaiders::spawn);
    public static final String HORDE_UNDEAD = registerHorde("undead", UndeadRaiders::spawn);

    private static final Set<String> BUILTIN_HORDE_TYPES = Set.of(HORDE_CAMEL, HORDE_BOAT, HORDE_HORSE, HORDE_SPIDER, HORDE_UNDEAD);

    public static Optional<ActiveHorde> spawnHorde(String horde, ServerLevel level, BlockPos position, @Nullable ServerPlayer target, int difficulty) {
        HordeSpawner spawner = HORDES.get(horde);
        Optional<ActiveHorde> spawned = spawner == null ? Optional.empty() : spawner.spawn(level, position, target, difficulty);
        if (target != null && spawned.isPresent()) {
            PlayerHordeData.get(target).markRaidStarted(horde, level.getGameTime());
        }
        return spawned;
    }

    public static boolean isHordeRegistered(String horde) {
        return HORDES.containsKey(horde);
    }

    public static List<String> getHordeNames() {
        return List.copyOf(HORDES.keySet());
    }

    public static Optional<ActiveHorde> spawnRandomHorde(ServerLevel level, BlockPos position, @Nullable ServerPlayer target, int difficulty) {
        List<String> hordeTypes = new ArrayList<>(getHordeNames());
        while (!hordeTypes.isEmpty()) {
            String hordeType = hordeTypes.remove(level.random.nextInt(hordeTypes.size()));
            if (!level.getBiome(position).is(TagKey.create(Registries.BIOME, ImmersivePillagers.locate(hordeType)))) {
                continue;
            }
            Optional<ActiveHorde> horde = spawnHorde(hordeType, level, position, target, difficulty);
            if (horde.isPresent()) {
                return horde;
            }
        }
        return Optional.empty();
    }

    public static boolean canReceiveBounty(ServerPlayer target) {
        return Config.getInstance().allowPlayerBounties
               && target.serverLevel().getDifficulty().getId() > 0
               && !target.serverLevel().isVillage(target.blockPosition());
    }

    public static boolean spawnBounty(ServerPlayer target) {
        if (!canReceiveBounty(target)) {
            return false;
        }
        ServerLevel level = target.serverLevel();
        BlockPos position = target.blockPosition();
        return spawnRandomHorde(level, position, target, 10).map(horde -> {
            addActiveHorde(horde);
            return true;
        }).orElse(false);
    }

    public static void markForReinforcedChestRaid(ServerPlayer player) {
        PlayerHordeData data = PlayerHordeData.get(player);
        if (data.markPillagerKilled()) {
            player.displayClientMessage(Component.translatable("message.immersive_pillagers.player_wanted"), true);
        }
        data.scheduleRaid(player.serverLevel().getGameTime() + 20L * 60L);
    }

    public static boolean summonWarHorde(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        if (level.getDifficulty() == Difficulty.PEACEFUL) {
            return false;
        }

        int casualDifficulty = SpawnManager.waveDifficulty(level, player.blockPosition());
        int difficulty = Math.max(1, Math.round(casualDifficulty * (float) Config.getInstance().warHordeDifficultyMultiplier));
        return spawnRandomHorde(level, player.blockPosition(), player, difficulty).map(horde -> {
            horde.setRegionToLiberate(player.blockPosition());
            addActiveHorde(horde);
            return true;
        }).orElse(false);
    }

    public static void openWantedPoster(ServerPlayer viewer, InteractionHand hand) {
        MinecraftServer server = viewer.getServer();
        if (server == null) {
            return;
        }
        List<OpenWantedPosterPacket.Entry> players = server.getPlayerList().getPlayers().stream()
                .sorted(Comparator
                        .comparing((ServerPlayer player) -> !player.getUUID().equals(viewer.getUUID()))
                        .thenComparing(player -> player.getGameProfile().getName(), String.CASE_INSENSITIVE_ORDER))
                .map(player -> new OpenWantedPosterPacket.Entry(
                        player.getUUID(),
                        player.getGameProfile().getName(),
                        PlayerHordeData.get(player).hasKilledPillager(),
                        canReceiveBounty(player)
                ))
                .toList();
        Handler.sendToPlayer(new OpenWantedPosterPacket(players, hand), viewer);
    }

    public static void handleWantedPosterAction(ServerPlayer sender, WantedPosterActionPacket packet) {
        MinecraftServer server = sender.getServer();
        if (server == null || !sender.getItemInHand(packet.hand()).is(ImmersivePillagersItems.WANTED_POSTER.get())) {
            return;
        }

        if (packet.action() == WantedPosterActionPacket.PARDON) {
            if (!sender.getUUID().equals(packet.target()) || !PlayerHordeData.get(sender).hasKilledPillager()) {
                return;
            }
            PlayerHordeData.get(sender).pardon();
            consumeWantedPoster(sender, packet.hand());
            sender.displayClientMessage(Component.translatable("message.immersive_pillagers.pardoned"), true);
            return;
        }

        ServerPlayer target = server.getPlayerList().getPlayer(packet.target());
        if (target == null) {
            return;
        }
        if (!Config.getInstance().allowPlayerBounties) {
            sender.displayClientMessage(Component.translatable("message.immersive_pillagers.bounty_disabled"), true);
            return;
        }
        if (!canReceiveBounty(target)) {
            sender.displayClientMessage(Component.translatable("message.immersive_pillagers.bounty_protected"), true);
            return;
        }
        if (!spawnBounty(target)) {
            sender.displayClientMessage(Component.translatable("message.immersive_pillagers.bounty_unavailable"), true);
            return;
        }

        consumeWantedPoster(sender, packet.hand());
        sender.displayClientMessage(Component.translatable("message.immersive_pillagers.bounty_sent", target.getGameProfile().getName()), true);
    }

    private static void consumeWantedPoster(ServerPlayer player, InteractionHand hand) {
        if (!player.getAbilities().instabuild) {
            player.getItemInHand(hand).shrink(1);
        }
    }

    public static void addActiveHorde(ActiveHorde horde) {
        ACTIVE_HORDES.put(horde.id(), horde);
    }

    public static List<ActiveHorde> getActiveHordes() {
        return List.copyOf(ACTIVE_HORDES.values());
    }

    public static int clearHordes() {
        int clearedHordes = ACTIVE_HORDES.size();
        for (ActiveHorde horde : ACTIVE_HORDES.values()) {
            horde.discard();
        }
        ACTIVE_HORDES.clear();
        return clearedHordes;
    }

    public static void tick(MinecraftServer server) {
        SpawnManager.tick(server);
        ACTIVE_HORDES.values().removeIf(horde -> !horde.tick());
    }

    public static Optional<? extends Player> getClosestPlayer(Entity entity) {
        return entity.level().players().stream().min(Comparator.comparingDouble(player -> player.distanceToSqr(entity)));
    }

    public static void onLivingEntityKilled(LivingEntity killed, Entity killer) {
        if (killer instanceof ServerPlayer player) {
            if (killed instanceof Pillager && !(killed instanceof UndeadPillager)) {
                if (PlayerHordeData.get(player).markPillagerKilled()) {
                    player.displayClientMessage(Component.translatable("message.immersive_pillagers.player_wanted"), true);
                }
            }

            ItemStack offhand = player.getOffhandItem();
            if (killed.getType().is(ImmersivePillagers.HUMANOID_ENTITY_TYPES) && offhand.is(ImmersivePillagersItems.CRUDE_TOTEM_OF_UNDYING.get())) {
                player.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(Items.TOTEM_OF_UNDYING));
                killed.level().playSound(null, killed.getX(), killed.getY(), killed.getZ(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 0.8F);
                awardCrudeTotemAwakened(player);
            }
        }
    }

    public static void awardHordeConquerorProgress(ServerPlayer player, String hordeType) {
        if (!BUILTIN_HORDE_TYPES.contains(hordeType)) {
            return;
        }

        MinecraftServer server = player.getServer();
        if (server == null) {
            return;
        }

        Advancement advancement = server.getAdvancements().getAdvancement(HORDE_CONQUEROR_ADVANCEMENT);
        if (advancement != null) {
            player.getAdvancements().award(advancement, hordeType);
        }
    }

    private static void awardCrudeTotemAwakened(ServerPlayer player) {
        MinecraftServer server = player.getServer();
        if (server == null) {
            return;
        }
        Advancement advancement = server.getAdvancements().getAdvancement(CRUDE_TOTEM_AWAKENED_ADVANCEMENT);
        if (advancement != null) {
            player.getAdvancements().award(advancement, "charged");
        }
    }
}
