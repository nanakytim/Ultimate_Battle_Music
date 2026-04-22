package net.nanaky.battle_music.registry;

import net.nanaky.battle_music.BattleMusicMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {
    public static SoundEvent BATTLE_MUSIC;
    public static SoundEvent BATTLE_VARIANT;
    public static SoundEvent BATTLE_BANDITS;
    public static SoundEvent BATTLE_NETHER;
    public static SoundEvent BATTLE_RAID;
    public static SoundEvent BATTLE_WARDEN;
    public static SoundEvent BATTLE_WITHER;
    public static SoundEvent BATTLE_DRAGON;

    public static void register() {
        BATTLE_MUSIC   = reg("battle_music");
        BATTLE_VARIANT = reg("battle_variant");
        BATTLE_BANDITS = reg("battle_illager");
        BATTLE_NETHER  = reg("battle_nether");
        BATTLE_RAID    = reg("battle_raid");
        BATTLE_WARDEN  = reg("battle_warden");
        BATTLE_WITHER  = reg("battle_wither");
        BATTLE_DRAGON  = reg("battle_dragon");
    }

    private static SoundEvent reg(String name) {
        Identifier id = Identifier.fromNamespaceAndPath(BattleMusicMod.MOD_ID, name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id,
                SoundEvent.createVariableRangeEvent(id));
    }
}