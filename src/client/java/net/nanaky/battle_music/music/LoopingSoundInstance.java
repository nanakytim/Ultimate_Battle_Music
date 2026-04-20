package net.nanaky.battle_music.music;

import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

public class LoopingSoundInstance extends AbstractSoundInstance implements TickableSoundInstance {

    public enum Phase { SUSTAIN, FADE_IN, FADE_OUT, GHOST, DEAD }

    private Phase phase;
    private int   fadeTick;
    private int   ghostTick;

    private final float targetVolume;
    private final int   reviveFadeInTicks;
    private final int   fadeOutTicks;
    private final int   ghostDurationTicks;
    private float suppressLevel  = 1.0f;
    private float suppressTarget = 1.0f;
    private static final float SUPPRESS_SPEED = 1.0f / 20f;
    private float targetPitch;
    private static final float PITCH_LERP_IN_SPEED  = 1.0f / 20f;
    private static final float PITCH_LERP_OUT_SPEED = 1.0f / 10f;

    public LoopingSoundInstance(SoundEvent event, float targetVolume, float pitch,
                                int reviveFadeInTicks, int fadeOutTicks, int ghostDurationTicks) {
        super(event.location(), SoundSource.MUSIC, RandomSource.create());
        this.targetVolume       = targetVolume;
        this.pitch              = pitch;
        this.targetPitch        = pitch;
        this.looping            = true;
        this.relative           = true;
        this.reviveFadeInTicks  = reviveFadeInTicks;
        this.fadeOutTicks       = fadeOutTicks;
        this.ghostDurationTicks = ghostDurationTicks;
        this.volume   = targetVolume;
        this.phase    = Phase.SUSTAIN;
        this.fadeTick = reviveFadeInTicks;
    }

    @Override
    public void tick() {
        float phaseVolume;
        switch (phase) {
            case FADE_IN -> {
                fadeTick++;
                phaseVolume = Math.min(1f, (float) fadeTick / Math.max(1, reviveFadeInTicks));
                if (fadeTick >= reviveFadeInTicks) {
                    phaseVolume = 1f;
                    phase = Phase.SUSTAIN;
                }
            }
            case FADE_OUT -> {
                fadeTick--;
                phaseVolume = Math.max(0f, (float) fadeTick / Math.max(1, fadeOutTicks));
                if (fadeTick <= 0) {
                    phaseVolume = 0f;
                    phase     = Phase.GHOST;
                    ghostTick = ghostDurationTicks;
                }
            }
            case GHOST -> {
                phaseVolume = 0f;
                if (--ghostTick <= 0) phase = Phase.DEAD;
            }
            default -> phaseVolume = 1f;
        }
        if (suppressLevel != suppressTarget) {
            float step = SUPPRESS_SPEED;
            suppressLevel = suppressTarget > suppressLevel
                    ? Math.min(suppressTarget, suppressLevel + step)
                    : Math.max(suppressTarget, suppressLevel - step);
        }

        volume = targetVolume * phaseVolume * suppressLevel;

        if (Math.abs(pitch - targetPitch) > 0.001f) {
            float speed = targetPitch < pitch ? PITCH_LERP_OUT_SPEED : PITCH_LERP_IN_SPEED;
            pitch += (targetPitch - pitch) * speed;
        } else {
            pitch = targetPitch;
        }
    }

    public void beginFadeOut(boolean useFade) {
        if (phase == Phase.DEAD || phase == Phase.GHOST) return;

        if (!useFade || fadeOutTicks <= 0) {
            volume    = 0f;
            phase     = Phase.GHOST;
            ghostTick = ghostDurationTicks;
            return;
        }

        fadeTick = (phase == Phase.FADE_IN)
                ? (int)(((float) fadeTick / reviveFadeInTicks) * fadeOutTicks)
                : fadeOutTicks;
        phase = Phase.FADE_OUT;
    }

    public void revive(boolean useFadeIn) {
        if (phase == Phase.DEAD) return;
        looping = true;

        if (useFadeIn && reviveFadeInTicks > 0) {
            fadeTick = 0;
            phase    = Phase.FADE_IN;
        } else {
            phase = Phase.SUSTAIN;
        }
    }

    public void suppress(boolean useFade) {
        suppressTarget = 0f;
        if (!useFade) suppressLevel = 0f;
    }

    public void activate(boolean useFade) {
        suppressTarget = 1f;
        if (!useFade) suppressLevel = 1f;
    }

    public void setTargetPitch(float newPitch, boolean useFade) {
        targetPitch = newPitch;
        if (!useFade) pitch = newPitch;
    }

    public boolean isRevivable() { return phase == Phase.GHOST || phase == Phase.FADE_OUT; }
    public Phase   getPhase()    { return phase; }

    @Override
    public boolean isStopped() { return phase == Phase.DEAD; }
}