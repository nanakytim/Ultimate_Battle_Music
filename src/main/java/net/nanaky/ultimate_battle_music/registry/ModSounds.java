package net.nanaky.ultimate_battle_music.registry;

import net.nanaky.ultimate_battle_music.UltimateBattleMusicMod;
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
<<<<<<< HEAD
    public static SoundEvent BATTLE_BOSS;
=======
    public static SoundEvent BATTLE_INVOKER;
    public static SoundEvent BATTLE_WARDEN;
    public static SoundEvent BATTLE_WITHER;
    public static SoundEvent BATTLE_DRAGON;
>>>>>>> d1d3ba7 (Fixed Illager tag and added Invoker boss)

    public static void register() {
        BATTLE_MUSIC   = reg("battle_music");
        BATTLE_VARIANT = reg("battle_variant");
        BATTLE_BANDITS = reg("battle_bandits");
        BATTLE_NETHER  = reg("battle_nether");
        BATTLE_RAID    = reg("battle_raid");
<<<<<<< HEAD
        BATTLE_BOSS    = reg("battle_boss");
=======
        BATTLE_INVOKER = reg("battle_invoker");
        BATTLE_WARDEN  = reg("battle_warden");
        BATTLE_WITHER  = reg("battle_wither");
        BATTLE_DRAGON  = reg("battle_dragon");
>>>>>>> d1d3ba7 (Fixed Illager tag and added Invoker boss)
    }

    private static SoundEvent reg(String name) {
        Identifier id = Identifier.fromNamespaceAndPath(UltimateBattleMusicMod.MOD_ID, name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id,
                SoundEvent.createVariableRangeEvent(id));
    }
}