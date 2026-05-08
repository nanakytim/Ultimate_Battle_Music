package net.nanaky.ultimate_battle_music;

import net.fabricmc.api.ModInitializer;
import net.nanaky.ultimate_battle_music.config.ConfigManager;
import net.nanaky.ultimate_battle_music.registry.ModSounds;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UltimateBattleMusicMod implements ModInitializer {
    public static final String MOD_ID = "ultimate_battle_music";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModSounds.register();
        ConfigManager.load();
    }
}