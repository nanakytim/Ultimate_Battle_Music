package net.nanaky.battle_music.config;

import net.nanaky.battle_music.BattleMusicMod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class BattleMusicConfig {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static BattleMusicConfig instance;

    public int     checkIntervalTicks     = 10;
    public boolean requireTargetingPlayer = true;
    public double bossRadius    = 100.0;
    public double normalRadius  = 16.0;
    public double farRadius     = 32.0;
    public double variantRadius = 16.0;
    public double banditRadius  = 32.0;
    public double creeperRadius = 3.0;
    public boolean enableMusic  = true;
    public MusicMode variantMode = MusicMode.ON;
    public MusicMode banditMode  = MusicMode.ON;
    public MusicMode netherMode  = MusicMode.ON;
    public MusicMode raidMode    = MusicMode.ON;
    public MusicMode wardenMode  = MusicMode.ON;
    public MusicMode witherMode  = MusicMode.ON;
    public MusicMode dragonMode  = MusicMode.ON;
    public float defaultVolume   = 0.5f;
    public float banditVolume    = 0.5f;
    public float netherVolume    = 0.5f;
    public float raidVolume      = 0.5f;
    public float wardenVolume    = 0.5f;
    public float witherVolume    = 0.5f;
    public float dragonVolume    = 0.5f;
    public float underwaterPitch = 0.75f;
    public boolean useFade            = true;
    public int     fadeOutTicks       = 20;
    public int     reviveFadeInTicks  = 10;
    public int     ghostDurationTicks = 120;

    public static void load() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve("battle_music.json");
        if (Files.exists(path)) {
            try {
                instance = GSON.fromJson(Files.readString(path), BattleMusicConfig.class);
            } catch (IOException e) {
                BattleMusicMod.LOGGER.error("Failed to load config, using defaults", e);
                instance = new BattleMusicConfig();
            }
        } else {
            instance = new BattleMusicConfig();
            save(path);
        }
    }

    private static void save(Path path) {
        try { Files.writeString(path, GSON.toJson(instance)); }
        catch (IOException e) { BattleMusicMod.LOGGER.error("Failed to save config", e); }
    }

    public static BattleMusicConfig getInstance() { return instance; }

    public int     getCheckIntervalTicks()    { return checkIntervalTicks; }
    public boolean isRequireTargetingPlayer() { return requireTargetingPlayer; }
    public double  getBossRadius()            { return bossRadius; }
    public double  getNormalRadius()          { return normalRadius; }
    public double  getFarRadius()             { return farRadius; }
    public double  getVariantRadius()         { return variantRadius; }
    public double  getBanditRadius()          { return banditRadius; }
    public double  getCreeperRadius()         { return creeperRadius; }
    public boolean isEnableMusic()            { return enableMusic; }
    public MusicMode getVariantMode()         { return variantMode != null ? variantMode : MusicMode.ON; }
    public MusicMode getBanditMode()          { return banditMode  != null ? banditMode  : MusicMode.ON; }
    public MusicMode getNetherMode()          { return netherMode  != null ? netherMode  : MusicMode.ON; }
    public MusicMode getRaidMode()            { return raidMode    != null ? raidMode    : MusicMode.ON; }
    public MusicMode getWardenMode()          { return wardenMode  != null ? wardenMode  : MusicMode.ON; }
    public MusicMode getWitherMode()          { return witherMode  != null ? witherMode  : MusicMode.ON; }
    public MusicMode getDragonMode()          { return dragonMode  != null ? dragonMode  : MusicMode.ON; }
    public float   getDefaultVolume()         { return defaultVolume; }
    public float   getBanditVolume()          { return banditVolume; }
    public float   getNetherVolume()          { return netherVolume; }
    public float   getRaidVolume()            { return raidVolume; }
    public float   getWardenVolume()          { return wardenVolume; }
    public float   getWitherVolume()          { return witherVolume; }
    public float   getDragonVolume()          { return dragonVolume; }
    public float   getUnderwaterPitch()       { return underwaterPitch; }
    public boolean isUseFade()                { return useFade; }
    public int     getFadeOutTicks()          { return fadeOutTicks; }
    public int     getReviveFadeInTicks()     { return reviveFadeInTicks; }
    public int     getGhostDurationTicks()    { return ghostDurationTicks; }
}