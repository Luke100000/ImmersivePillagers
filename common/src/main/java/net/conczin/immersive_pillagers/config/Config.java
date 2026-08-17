package net.conczin.immersive_pillagers.config;

public final class Config extends JsonConfig {
    private static final Config INSTANCE = loadOrCreate();

    public static Config getInstance() {
        return INSTANCE;
    }

    @SuppressWarnings("unused")
    public String README = "https://github.com/Luke100000/ImmersivePillagers/wiki/Config";

    public long ticksBetweenWaves = 24000L * 10L;
    public boolean allowPlayerBounties = true;

    // Difficulty factor for all wave types
    public double baseDifficultyFactor = 2.0;

    // Additional multiplier for the war horde
    public double warHordeDifficultyMultiplier = 5.0;

    public double instrumentChance = 0.2;
    public double rotaryCannonChance = 0.3;
}
