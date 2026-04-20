package net.nanaky.battle_music;

import net.nanaky.battle_music.combat.CombatDetector;
import net.nanaky.battle_music.music.MusicManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class BattleMusicClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            CombatDetector.tick();
            MusicManager.tick();
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) ->
                client.execute(() -> {
                    MusicManager.stopAll();
                    CombatDetector.reset();
                }));
    }
}