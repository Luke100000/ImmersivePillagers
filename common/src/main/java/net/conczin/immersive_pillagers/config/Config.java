package net.conczin.immersive_pillagers.config;

public final class Config extends JsonConfig {
    private static final Config INSTANCE = loadOrCreate();

    public static Config getInstance() {
        return INSTANCE;
    }

    @SuppressWarnings("unused")
    public String README = "https://github.com/Luke100000/ImmersivePillagers/wiki/Config";

    public long ticksBetweenWaves = 20L * 60L * 10L;
    public double difficultyFactor = 1.0;
}
