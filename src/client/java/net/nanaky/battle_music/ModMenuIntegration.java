package net.nanaky.battle_music;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.network.chat.Component;
import net.nanaky.battle_music.config.BattleMusicConfig;
import net.nanaky.battle_music.config.ConfigManager;
import net.nanaky.battle_music.config.MusicMode;
import net.nanaky.battle_music.music.MusicManager;

public class ModMenuIntegration implements ModMenuApi {

    // Human-readable labels shown in the dropdown for each MusicMode value
    private static Component modeLabel(MusicMode mode) {
        return switch (mode) {
            case ON     -> Component.literal("ON (unique track)");
            case FALLBACK -> Component.literal("NORMAL (fallback)");
            case OFF    -> Component.literal("OFF (no music)");
        };
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (!FabricLoader.getInstance().isModLoaded("cloth-config")) {
            return parent -> null;
        }
        return parent -> {
            BattleMusicConfig cfg = ConfigManager.INSTANCE;
            BattleMusicConfig def = new BattleMusicConfig();

            ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.literal("Battle Music"))
                .setSavingRunnable(() -> {
                    ConfigManager.save();
                    MusicManager.onConfigChanged();
                });

            ConfigEntryBuilder eb = builder.entryBuilder();

            ConfigCategory music = builder.getOrCreateCategory(Component.literal("Music"));

            music.addEntry(eb.startBooleanToggle(
                    Component.literal("ENABLE BATTLE MUSIC"), cfg.enableMusic)
                .setDefaultValue(def.enableMusic)
                .setTooltip(Component.literal("Master toggle. If off, no battle music plays at all."))
                .setSaveConsumer(v -> cfg.enableMusic = v)
                .build());

            music.addEntry(eb.startEnumSelector(
                    Component.literal("Variant Music"),
                    MusicMode.class,
                    cfg.variantMode)
                .setDefaultValue(def.variantMode)
                .setTooltip(Component.literal(
                    "ON = unique variant track | NORMAL = fallback to default | OFF = silence for variants"))
                .setEnumNameProvider(e -> modeLabel((MusicMode) e))
                .setSaveConsumer(v -> cfg.variantMode = v)
                .build());

            music.addEntry(eb.startEnumSelector(
                    Component.literal("Illager Music"),
                    MusicMode.class,
                    cfg.banditMode)
                .setDefaultValue(def.banditMode)
                .setTooltip(Component.literal(
                    "ON = unique illager track | NORMAL = fallback to default | OFF = silence for illagers"))
                .setEnumNameProvider(e -> modeLabel((MusicMode) e))
                .setSaveConsumer(v -> cfg.banditMode = v)
                .build());

            music.addEntry(eb.startEnumSelector(
                    Component.literal("Nether Music"),
                    MusicMode.class,
                    cfg.netherMode)
                .setDefaultValue(def.netherMode)
                .setTooltip(Component.literal(
                    "ON = unique nether track | NORMAL = fallback to default | OFF = silence in nether combat"))
                .setEnumNameProvider(e -> modeLabel((MusicMode) e))
                .setSaveConsumer(v -> cfg.netherMode = v)
                .build());

            music.addEntry(eb.startEnumSelector(
                    Component.literal("Raid Music"),
                    MusicMode.class,
                    cfg.raidMode)
                .setDefaultValue(def.raidMode)
                .setTooltip(Component.literal(
                    "ON = unique raid track | NORMAL = fallback to default | OFF = silence during raids"))
                .setEnumNameProvider(e -> modeLabel((MusicMode) e))
                .setSaveConsumer(v -> cfg.raidMode = v)
                .build());

            music.addEntry(eb.startEnumSelector(
                    Component.literal("Boss Music"),
                    MusicMode.class,
                    cfg.bossMode)
                .setDefaultValue(def.bossMode)
                .setTooltip(Component.literal(
                    "ON = unique boss track | NORMAL = fallback to default | OFF = silence during boss fights"))
                .setEnumNameProvider(e -> modeLabel((MusicMode) e))
                .setSaveConsumer(v -> cfg.bossMode = v)
                .build());

            ConfigCategory detection = builder.getOrCreateCategory(Component.literal("Detection Type"));

            detection.addEntry(eb.startBooleanToggle(
                    Component.literal("Require Mob Targeting Player"), cfg.requireTargetingPlayer)
                .setDefaultValue(def.requireTargetingPlayer)
                .setTooltip(Component.literal("Only trigger music when a mob is actively targeting you."))
                .setSaveConsumer(v -> cfg.requireTargetingPlayer = v)
                .build());

            detection.addEntry(eb.startIntSlider(
                    Component.literal("Check Interval (ticks)"), cfg.checkIntervalTicks, 1, 40)
                .setDefaultValue(def.checkIntervalTicks)
                .setTooltip(Component.literal("How often combat is checked. 10 = every 0.5s."))
                .setSaveConsumer(v -> cfg.checkIntervalTicks = v)
                .build());

            ConfigCategory radii = builder.getOrCreateCategory(Component.literal("Detection Radius"));

            radii.addEntry(eb.startIntSlider(
                    Component.literal("Normal Mob Radius"), (int) cfg.normalRadius, 1, 64)
                .setDefaultValue((int) def.normalRadius)
                .setSaveConsumer(v -> cfg.normalRadius = v)
                .build());

            radii.addEntry(eb.startIntSlider(
                    Component.literal("Variant Mob Radius"), (int) cfg.variantRadius, 1, 64)
                .setDefaultValue((int) def.variantRadius)
                .setSaveConsumer(v -> cfg.variantRadius = v)
                .build());

            radii.addEntry(eb.startIntSlider(
                    Component.literal("Illager Radius"), (int) cfg.banditRadius, 1, 64)
                .setDefaultValue((int) def.banditRadius)
                .setSaveConsumer(v -> cfg.banditRadius = v)
                .build());

            radii.addEntry(eb.startIntSlider(
                    Component.literal("Far Hostile Radius"), (int) cfg.farRadius, 1, 128)
                .setDefaultValue((int) def.farRadius)
                .setSaveConsumer(v -> cfg.farRadius = v)
                .build());

            radii.addEntry(eb.startIntSlider(
                    Component.literal("Creeper Radius"), (int) cfg.creeperRadius, 1, 32)
                .setDefaultValue((int) def.creeperRadius)
                .setTooltip(Component.literal("Creepers only trigger music very close by default."))
                .setSaveConsumer(v -> cfg.creeperRadius = v)
                .build());

            radii.addEntry(eb.startIntSlider(
                    Component.literal("Boss Radius"), (int) cfg.bossRadius, 1, 256)
                .setDefaultValue((int) def.bossRadius)
                .setTooltip(Component.literal("Detection Radius for Wither, Warden, Ender Dragon."))
                .setSaveConsumer(v -> cfg.bossRadius = v)
                .build());

            ConfigCategory volume = builder.getOrCreateCategory(Component.literal("Volume"));

            volume.addEntry(eb.startIntSlider(
                    Component.literal("Default Volume (%)"), (int)(cfg.defaultVolume * 100), 0, 100)
                .setDefaultValue((int)(def.defaultVolume * 100))
                .setTooltip(Component.literal("Volume for Overworld Normal Battle Music."))
                .setSaveConsumer(v -> cfg.defaultVolume = v / 100f)
                .build());

            volume.addEntry(eb.startIntSlider(
                    Component.literal("Illager Volume (%)"), (int)(cfg.banditVolume * 100), 0, 100)
                .setDefaultValue((int)(def.banditVolume * 100))
                .setSaveConsumer(v -> cfg.banditVolume = v / 100f)
                .build());

            volume.addEntry(eb.startIntSlider(
                    Component.literal("Raid Volume (%)"), (int)(cfg.raidVolume * 100), 0, 100)
                .setDefaultValue((int)(def.raidVolume * 100))
                .setSaveConsumer(v -> cfg.raidVolume = v / 100f)
                .build());

            volume.addEntry(eb.startIntSlider(
                    Component.literal("Nether Volume (%)"), (int)(cfg.netherVolume * 100), 0, 100)
                .setDefaultValue((int)(def.netherVolume * 100))
                .setSaveConsumer(v -> cfg.netherVolume = v / 100f)
                .build());

            volume.addEntry(eb.startIntSlider(
                    Component.literal("Boss Volume (%)"), (int)(cfg.bossVolume * 100), 0, 100)
                .setDefaultValue((int)(def.bossVolume * 100))
                .setSaveConsumer(v -> cfg.bossVolume = v / 100f)
                .build());

            volume.addEntry(eb.startIntSlider(
                    Component.literal("Fluid Pitch (%)"), (int)(cfg.underwaterPitch * 100), 50, 200)
                .setDefaultValue((int)(def.underwaterPitch * 100))
                .setTooltip(Component.literal("Pitch when inside Water or Lava. 75 = lower/deeper tone."))
                .setSaveConsumer(v -> cfg.underwaterPitch = v / 100f)
                .build());

            ConfigCategory fade = builder.getOrCreateCategory(Component.literal("Fade & Ghost"));

            fade.addEntry(eb.startBooleanToggle(
                    Component.literal("Use Fade"), cfg.useFade)
                .setDefaultValue(def.useFade)
                .setTooltip(Component.literal("Enable fade out and ghost revival. Disable for hard cuts."))
                .setSaveConsumer(v -> cfg.useFade = v)
                .build());

            fade.addEntry(eb.startIntSlider(
                    Component.literal("Fade Out (ticks)"), cfg.fadeOutTicks, 1, 200)
                .setDefaultValue(def.fadeOutTicks)
                .setTooltip(Component.literal("How long music takes to fade out. 60 = 3 seconds."))
                .setSaveConsumer(v -> cfg.fadeOutTicks = v)
                .build());

            fade.addEntry(eb.startIntSlider(
                    Component.literal("Revive Fade In (ticks)"), cfg.reviveFadeInTicks, 1, 200)
                .setDefaultValue(def.reviveFadeInTicks)
                .setTooltip(Component.literal("How long music fades back in on ghost revive. 40 = 2 seconds."))
                .setSaveConsumer(v -> cfg.reviveFadeInTicks = v)
                .build());

            fade.addEntry(eb.startIntSlider(
                    Component.literal("Ghost Duration (ticks)"), cfg.ghostDurationTicks, 1, 600)
                .setDefaultValue(def.ghostDurationTicks)
                .setTooltip(Component.literal("How long track loops silently before stopping. 200 = 10 seconds."))
                .setSaveConsumer(v -> cfg.ghostDurationTicks = v)
                .build());

            return builder.build();
        };
    }
}