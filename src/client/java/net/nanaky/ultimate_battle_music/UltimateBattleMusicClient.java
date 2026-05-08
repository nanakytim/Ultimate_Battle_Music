package net.nanaky.ultimate_battle_music;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.nanaky.ultimate_battle_music.combat.CombatDetector;
import net.nanaky.ultimate_battle_music.music.MusicManager;

public class UltimateBattleMusicClient implements ClientModInitializer {
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