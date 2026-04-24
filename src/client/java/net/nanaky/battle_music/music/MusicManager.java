package net.nanaky.battle_music.music;

import net.nanaky.battle_music.combat.CombatState;
import net.nanaky.battle_music.config.BattleMusicConfig;
import net.nanaky.battle_music.config.ConfigManager;
import net.nanaky.battle_music.registry.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;

import java.util.*;

public class MusicManager {

    private static final Map<CombatState, LoopingSoundInstance> managedSounds =
            new EnumMap<>(CombatState.class);

    private static Set<CombatState> activeStates = EnumSet.noneOf(CombatState.class);
    private static CombatState currentAudibleState = CombatState.NONE;
    private static CombatState lockedPeer          = CombatState.NONE;

    private static int     fluidPitchDelayTick = 0;
    private static boolean wasInFluid          = false;
    private static final int FLUID_PITCH_DELAY = 50;

    public static void tick() {
        managedSounds.entrySet().removeIf(e -> e.getValue().isStopped());

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            BattleMusicConfig cfg = ConfigManager.getInstance();
            boolean inFluid       = mc.player.isUnderWater()
                                 || mc.player.isEyeInFluid(net.minecraft.tags.FluidTags.LAVA);

            if (inFluid != wasInFluid) {
                fluidPitchDelayTick = 0;
                wasInFluid = inFluid;
            } else if (fluidPitchDelayTick < FLUID_PITCH_DELAY) {
                fluidPitchDelayTick++;
            }

            float targetPitch = (fluidPitchDelayTick >= FLUID_PITCH_DELAY)
                    ? (inFluid ? cfg.getUnderwaterPitch() : 1.0f)
                    : (wasInFluid ? cfg.getUnderwaterPitch() : 1.0f);

            for (LoopingSoundInstance sound : managedSounds.values()) {
                if (!sound.isStopped()) {
                    sound.setTargetPitch(targetPitch, false);
                }
            }
        }
    }

    public static void onConfigChanged() {
        BattleMusicConfig cfg = ConfigManager.getInstance();
        if (!cfg.isEnableMusic()) {
            stopAll();
            return;
        }
        if (!activeStates.isEmpty()) {
            onCombatStatesChanged(activeStates);
        }
    }

    public static void onCombatStatesChanged(Set<CombatState> newStates) {
        BattleMusicConfig cfg     = ConfigManager.getInstance();
        boolean           useFade = cfg.isUseFade();

        if (!cfg.isEnableMusic()) {
            stopAll();
            return;
        }

        Set<CombatState> removed = EnumSet.noneOf(CombatState.class);
        if (!activeStates.isEmpty()) removed.addAll(activeStates);
        removed.removeAll(newStates);

        Set<CombatState> added = newStates.isEmpty()
                ? EnumSet.noneOf(CombatState.class)
                : EnumSet.copyOf(newStates);
        added.removeAll(activeStates);

        CombatState desiredAudible = resolveAudibleState(newStates, cfg);

        for (CombatState state : removed) {
            LoopingSoundInstance sound = managedSounds.get(state);
            if (sound == null) continue;
            if (isPeer(state) && state != lockedPeer) continue;

            sound.beginFadeOut(useFade);
        }

        for (CombatState state : added) {
            LoopingSoundInstance existing = managedSounds.get(state);
            boolean willBeAudible         = (state == desiredAudible);

            boolean staleSuppressed = existing != null && !existing.isRevivable()
                                    && !existing.isStopped() && isPeer(state) && willBeAudible;

            if (staleSuppressed) {
                existing.beginFadeOut(false);
                existing = null;
            }

            if (existing != null && existing.isRevivable()) {
                existing.revive(useFade && willBeAudible);
                if (willBeAudible) existing.activate(useFade);
                else               existing.suppress(false);
            } else if (existing == null || existing.isStopped()) {
                SoundEvent sound = resolveSound(state, cfg);
                if (sound != null) {
                    LoopingSoundInstance instance = new LoopingSoundInstance(
                            sound,
                            resolveVolume(state, cfg),
                            1.0f,
                            cfg.getReviveFadeInTicks(),
                            cfg.getFadeOutTicks(),
                            cfg.getGhostDurationTicks()
                    );
                    if (!willBeAudible) instance.suppress(false);
                    managedSounds.put(state, instance);
                    Minecraft.getInstance().getSoundManager().play(instance);
                }
            }
        }

        activeStates        = newStates.isEmpty() ? EnumSet.noneOf(CombatState.class) : EnumSet.copyOf(newStates);
        currentAudibleState = desiredAudible;

        for (CombatState state : activeStates) {
            LoopingSoundInstance sound = managedSounds.get(state);
            if (sound != null && !sound.isStopped()) {
                if (state == currentAudibleState) sound.activate(useFade);
                else                              sound.suppress(useFade);
            }
        }

        if (!activeStates.isEmpty()) {
            Minecraft.getInstance().getMusicManager().stopPlaying();
        }
    }

    public static void stopAll() {
        managedSounds.values().forEach(s -> s.beginFadeOut(false));
        managedSounds.clear();
        activeStates        = EnumSet.noneOf(CombatState.class);
        currentAudibleState = CombatState.NONE;
        lockedPeer          = CombatState.NONE;
    }

    private static boolean isPeer(CombatState s) {
        return s == CombatState.OVERWORLD_NORMAL || s == CombatState.OVERWORLD_VARIANT;
    }

    private static CombatState resolveAudibleState(Set<CombatState> states, BattleMusicConfig cfg) {
        if (states.isEmpty()) return CombatState.NONE;

        if (states.contains(CombatState.ENDER_DRAGON))     return CombatState.ENDER_DRAGON;
        if (states.contains(CombatState.WITHER))           return CombatState.WITHER;
        if (states.contains(CombatState.WARDEN))           return CombatState.WARDEN;
        if (states.contains(CombatState.RAID))             return CombatState.RAID;
        if (states.contains(CombatState.OVERWORLD_BANDIT)) return CombatState.OVERWORLD_BANDIT;
        if (states.contains(CombatState.NETHER))           return CombatState.NETHER;

        boolean hasVariant = states.contains(CombatState.OVERWORLD_VARIANT);
        boolean hasNormal  = states.contains(CombatState.OVERWORLD_NORMAL);
        if (!hasVariant && !hasNormal) return CombatState.NONE;

        // Lock still alive — keep playing whatever won first, regardless of which mobs remain
        if (lockedPeer != CombatState.NONE) {
            LoopingSoundInstance locked = managedSounds.get(lockedPeer);
            if (locked != null && !locked.isStopped()) return lockedPeer;
            // Lock expired — fall through to pick fresh
            lockedPeer = CombatState.NONE;
        }

        // No lock — first play, use tag to decide song
        CombatState winner = hasVariant ? CombatState.OVERWORLD_VARIANT : CombatState.OVERWORLD_NORMAL;
        lockedPeer = winner;
        return winner;
    }

    private static SoundEvent resolveSound(CombatState state, BattleMusicConfig cfg) {
        return switch (state) {
            case OVERWORLD_VARIANT -> switch (cfg.getVariantMode()) {
                case ON       -> ModSounds.BATTLE_VARIANT;
                case FALLBACK -> ModSounds.BATTLE_MUSIC;
                case OFF      -> null;
            };
            case OVERWORLD_BANDIT -> switch (cfg.getBanditMode()) {
                case ON       -> ModSounds.BATTLE_BANDITS;
                case FALLBACK -> ModSounds.BATTLE_MUSIC;
                case OFF      -> null;
            };
            case NETHER -> switch (cfg.getNetherMode()) {
                case ON       -> ModSounds.BATTLE_NETHER;
                case FALLBACK -> ModSounds.BATTLE_MUSIC;
                case OFF      -> null;
            };
            case RAID -> switch (cfg.getRaidMode()) {
                case ON       -> ModSounds.BATTLE_RAID;
                case FALLBACK -> ModSounds.BATTLE_MUSIC;
                case OFF      -> null;
            };
            case WARDEN -> switch (cfg.getWardenMode()) {
                case ON       -> ModSounds.BATTLE_WARDEN;
                case FALLBACK -> ModSounds.BATTLE_MUSIC;
                case OFF      -> null;
            };
            case WITHER -> switch (cfg.getWitherMode()) {
                case ON       -> ModSounds.BATTLE_WITHER;
                case FALLBACK -> ModSounds.BATTLE_MUSIC;
                case OFF      -> null;
            };
            case ENDER_DRAGON -> switch (cfg.getDragonMode()) {
                case ON       -> ModSounds.BATTLE_DRAGON;
                case FALLBACK -> ModSounds.BATTLE_MUSIC;
                case OFF      -> null;
            };
            case OVERWORLD_NORMAL -> ModSounds.BATTLE_MUSIC;
            default               -> null;
        };
    }

    private static float resolveVolume(CombatState state, BattleMusicConfig cfg) {
        return switch (state) {
            case ENDER_DRAGON     -> cfg.getDragonVolume();
            case WITHER           -> cfg.getWitherVolume();
            case WARDEN           -> cfg.getWardenVolume();
            case RAID             -> cfg.getRaidVolume();
            case NETHER           -> cfg.getNetherVolume();
            case OVERWORLD_BANDIT -> cfg.getBanditVolume();
            default               -> cfg.getDefaultVolume();
        };
    }
}