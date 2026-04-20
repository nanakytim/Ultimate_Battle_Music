package net.nanaky.battle_music;

import net.nanaky.battle_music.config.ConfigManager;
import net.nanaky.battle_music.registry.ModSounds;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BattleMusicMod implements ModInitializer {
    public static final String MOD_ID = "battle_music";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModSounds.register();
        ConfigManager.load();
    }
}