package net.nanaky.battle_music.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.nanaky.battle_music.BattleMusicMod;

import java.io.*;
import java.nio.file.Path;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH =
            FabricLoader.getInstance().getConfigDir().resolve("battle_music.json");

    public static BattleMusicConfig INSTANCE = new BattleMusicConfig();

    public static void load() {
        if (!CONFIG_PATH.toFile().exists()) {
            save();
            return;
        }
        try (Reader r = new FileReader(CONFIG_PATH.toFile())) {
            BattleMusicConfig loaded = GSON.fromJson(r, BattleMusicConfig.class);
            if (loaded != null) {
                INSTANCE.checkIntervalTicks     = loaded.checkIntervalTicks;
                INSTANCE.requireTargetingPlayer = loaded.requireTargetingPlayer;
                INSTANCE.bossRadius             = loaded.bossRadius;
                INSTANCE.normalRadius           = loaded.normalRadius;
                INSTANCE.farRadius              = loaded.farRadius;
                INSTANCE.variantRadius          = loaded.variantRadius;
                INSTANCE.banditRadius           = loaded.banditRadius;
                INSTANCE.creeperRadius          = loaded.creeperRadius;
                INSTANCE.netherRadius           = loaded.netherRadius;
                INSTANCE.enableMusic            = loaded.enableMusic;
                INSTANCE.enableVariant          = loaded.enableVariant;
                INSTANCE.enableBandit           = loaded.enableBandit;
                INSTANCE.enableNether           = loaded.enableNether;
                INSTANCE.enableBoss             = loaded.enableBoss;
                INSTANCE.defaultVolume          = loaded.defaultVolume;
                INSTANCE.banditVolume           = loaded.banditVolume;
                INSTANCE.netherVolume           = loaded.netherVolume;
                INSTANCE.bossVolume             = loaded.bossVolume;
                INSTANCE.underwaterPitch        = loaded.underwaterPitch;
                INSTANCE.useFade                = loaded.useFade;
                INSTANCE.fadeOutTicks           = loaded.fadeOutTicks;
                INSTANCE.reviveFadeInTicks      = loaded.reviveFadeInTicks;
                INSTANCE.ghostDurationTicks     = loaded.ghostDurationTicks;
            }
        } catch (Exception e) {
            BattleMusicMod.LOGGER.error("[BattleMusic] Failed to load config: {}", e.toString());
        }
    }

    public static void save() {
        try (Writer w = new FileWriter(CONFIG_PATH.toFile())) {
            GSON.toJson(INSTANCE, w);
        } catch (Exception e) {
            BattleMusicMod.LOGGER.error("[BattleMusic] Failed to save config: {}", e.toString());
        }
    }

    public static BattleMusicConfig getInstance() { return INSTANCE; }
}