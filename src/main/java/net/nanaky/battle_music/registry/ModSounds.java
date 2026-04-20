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
    public static SoundEvent BATTLE_BOSS;

    public static void register() {
        BATTLE_MUSIC   = reg("battle_music");
        BATTLE_VARIANT = reg("battle_variant");
        BATTLE_BANDITS = reg("battle_bandits");
        BATTLE_NETHER  = reg("battle_nether");
        BATTLE_RAID    = reg("battle_raid");
        BATTLE_BOSS    = reg("battle_boss");
    }

    private static SoundEvent reg(String name) {
        Identifier id = Identifier.fromNamespaceAndPath(BattleMusicMod.MOD_ID, name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id,
                SoundEvent.createVariableRangeEvent(id));
    }
}