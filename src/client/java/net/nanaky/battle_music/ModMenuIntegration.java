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

import java.util.Optional;

public class ModMenuIntegration implements ModMenuApi {

    private static Component modeLabel(MusicMode mode) {
        return switch (mode) {
            case ON       -> Component.translatable("battle_music.config.mode.on");
            case FALLBACK -> Component.translatable("battle_music.config.mode.fallback");
            case OFF      -> Component.translatable("battle_music.config.mode.off");
        };
    }

    private static Optional<Component[]> modeTooltip(MusicMode mode) {
        return Optional.of(new Component[]{ switch (mode) {
            case ON       -> Component.translatable("battle_music.config.mode.on.tooltip");
            case FALLBACK -> Component.translatable("battle_music.config.mode.fallback.tooltip");
            case OFF      -> Component.translatable("battle_music.config.mode.off.tooltip");
        }});
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
                .setTitle(Component.translatable("battle_music.config.title"))
                .setSavingRunnable(() -> {
                    ConfigManager.save();
                    MusicManager.onConfigChanged();
                });

            ConfigEntryBuilder eb = builder.entryBuilder();

            ConfigCategory music = builder.getOrCreateCategory(Component.translatable("battle_music.config.category.music"));

            music.addEntry(eb.startBooleanToggle(
                    Component.translatable("battle_music.config.music.enable"), cfg.enableMusic)
                .setDefaultValue(def.enableMusic)
                .setTooltip(Component.translatable("battle_music.config.music.enable.tooltip"))
                .setSaveConsumer(v -> cfg.enableMusic = v)
                .build());

            music.addEntry(eb.startEnumSelector(
                    Component.translatable("battle_music.config.music.variant_mode"),
                    MusicMode.class,
                    cfg.variantMode)
                .setDefaultValue(def.variantMode)
                .setEnumNameProvider(e -> modeLabel((MusicMode) e))
                .setTooltipSupplier(mode -> modeTooltip((MusicMode) mode))
                .setSaveConsumer(v -> cfg.variantMode = v)
                .build());

            music.addEntry(eb.startEnumSelector(
                    Component.translatable("battle_music.config.music.illager_mode"),
                    MusicMode.class,
                    cfg.illagerMode)
                .setDefaultValue(def.illagerMode)
                .setEnumNameProvider(e -> modeLabel((MusicMode) e))
                .setTooltipSupplier(mode -> modeTooltip((MusicMode) mode))
                .setSaveConsumer(v -> cfg.illagerMode = v)
                .build());

            music.addEntry(eb.startEnumSelector(
                    Component.translatable("battle_music.config.music.nether_mode"),
                    MusicMode.class,
                    cfg.netherMode)
                .setDefaultValue(def.netherMode)
                .setEnumNameProvider(e -> modeLabel((MusicMode) e))
                .setTooltipSupplier(mode -> modeTooltip((MusicMode) mode))
                .setSaveConsumer(v -> cfg.netherMode = v)
                .build());

            music.addEntry(eb.startEnumSelector(
                    Component.translatable("battle_music.config.music.raid_mode"),
                    MusicMode.class,
                    cfg.raidMode)
                .setDefaultValue(def.raidMode)
                .setEnumNameProvider(e -> modeLabel((MusicMode) e))
                .setTooltipSupplier(mode -> modeTooltip((MusicMode) mode))
                .setSaveConsumer(v -> cfg.raidMode = v)
                .build());

            ConfigCategory detection = builder.getOrCreateCategory(Component.translatable("battle_music.config.category.detection"));

            detection.addEntry(eb.startBooleanToggle(
                    Component.translatable("battle_music.config.detection.require_targeting"), cfg.requireTargetingPlayer)
                .setDefaultValue(def.requireTargetingPlayer)
                .setTooltip(Component.translatable("battle_music.config.detection.require_targeting.tooltip"))
                .setSaveConsumer(v -> cfg.requireTargetingPlayer = v)
                .build());

            detection.addEntry(eb.startIntSlider(
                    Component.translatable("battle_music.config.detection.check_interval"), cfg.checkIntervalTicks, 1, 40)
                .setDefaultValue(def.checkIntervalTicks)
                .setTooltip(Component.translatable("battle_music.config.detection.check_interval.tooltip"))
                .setSaveConsumer(v -> cfg.checkIntervalTicks = v)
                .build());

            ConfigCategory radii = builder.getOrCreateCategory(Component.translatable("battle_music.config.category.radius"));

            radii.addEntry(eb.startIntSlider(
                    Component.translatable("battle_music.config.radius.normal"), (int) cfg.normalRadius, 1, 64)
                .setDefaultValue((int) def.normalRadius)
                .setSaveConsumer(v -> cfg.normalRadius = v)
                .build());

            radii.addEntry(eb.startIntSlider(
                    Component.translatable("battle_music.config.radius.variant"), (int) cfg.variantRadius, 1, 64)
                .setDefaultValue((int) def.variantRadius)
                .setSaveConsumer(v -> cfg.variantRadius = v)
                .build());

            radii.addEntry(eb.startIntSlider(
                    Component.translatable("battle_music.config.radius.illager"), (int) cfg.illagerRadius, 1, 64)
                .setDefaultValue((int) def.illagerRadius)
                .setSaveConsumer(v -> cfg.illagerRadius = v)
                .build());

            radii.addEntry(eb.startIntSlider(
                    Component.translatable("battle_music.config.radius.far"), (int) cfg.farRadius, 1, 128)
                .setDefaultValue((int) def.farRadius)
                .setSaveConsumer(v -> cfg.farRadius = v)
                .build());

            radii.addEntry(eb.startIntSlider(
                    Component.translatable("battle_music.config.radius.creeper"), (int) cfg.creeperRadius, 1, 32)
                .setDefaultValue((int) def.creeperRadius)
                .setTooltip(Component.translatable("battle_music.config.radius.creeper.tooltip"))
                .setSaveConsumer(v -> cfg.creeperRadius = v)
                .build());

            radii.addEntry(eb.startIntSlider(
                    Component.translatable("battle_music.config.radius.boss"), (int) cfg.bossRadius, 1, 256)
                .setDefaultValue((int) def.bossRadius)
                .setTooltip(Component.translatable("battle_music.config.radius.boss.tooltip"))
                .setSaveConsumer(v -> cfg.bossRadius = v)
                .build());

            ConfigCategory bosses = builder.getOrCreateCategory(Component.translatable("battle_music.config.category.bosses"));

            bosses.addEntry(eb.startEnumSelector(
                    Component.translatable("battle_music.config.bosses.warden_mode"),
                    MusicMode.class,
                    cfg.wardenMode)
                .setDefaultValue(def.wardenMode)
                .setEnumNameProvider(e -> modeLabel((MusicMode) e))
                .setTooltipSupplier(mode -> modeTooltip((MusicMode) mode))
                .setSaveConsumer(v -> cfg.wardenMode = v)
                .build());

            bosses.addEntry(eb.startIntSlider(
                    Component.translatable("battle_music.config.bosses.warden_volume"), (int)(cfg.wardenVolume * 100), 0, 100)
                .setDefaultValue((int)(def.wardenVolume * 100))
                .setSaveConsumer(v -> cfg.wardenVolume = v / 100f)
                .build());

            bosses.addEntry(eb.startEnumSelector(
                    Component.translatable("battle_music.config.bosses.wither_mode"),
                    MusicMode.class,
                    cfg.witherMode)
                .setDefaultValue(def.witherMode)
                .setEnumNameProvider(e -> modeLabel((MusicMode) e))
                .setTooltipSupplier(mode -> modeTooltip((MusicMode) mode))
                .setSaveConsumer(v -> cfg.witherMode = v)
                .build());

            bosses.addEntry(eb.startIntSlider(
                    Component.translatable("battle_music.config.bosses.wither_volume"), (int)(cfg.witherVolume * 100), 0, 100)
                .setDefaultValue((int)(def.witherVolume * 100))
                .setSaveConsumer(v -> cfg.witherVolume = v / 100f)
                .build());

            bosses.addEntry(eb.startEnumSelector(
                    Component.translatable("battle_music.config.bosses.dragon_mode"),
                    MusicMode.class,
                    cfg.dragonMode)
                .setDefaultValue(def.dragonMode)
                .setEnumNameProvider(e -> modeLabel((MusicMode) e))
                .setTooltipSupplier(mode -> modeTooltip((MusicMode) mode))
                .setSaveConsumer(v -> cfg.dragonMode = v)
                .build());

            bosses.addEntry(eb.startIntSlider(
                    Component.translatable("battle_music.config.bosses.dragon_volume"), (int)(cfg.dragonVolume * 100), 0, 100)
                .setDefaultValue((int)(def.dragonVolume * 100))
                .setSaveConsumer(v -> cfg.dragonVolume = v / 100f)
                .build());

            ConfigCategory volume = builder.getOrCreateCategory(Component.translatable("battle_music.config.category.volume"));

            volume.addEntry(eb.startIntSlider(
                    Component.translatable("battle_music.config.volume.default"), (int)(cfg.defaultVolume * 100), 0, 100)
                .setDefaultValue((int)(def.defaultVolume * 100))
                .setTooltip(Component.translatable("battle_music.config.volume.default.tooltip"))
                .setSaveConsumer(v -> cfg.defaultVolume = v / 100f)
                .build());

            volume.addEntry(eb.startIntSlider(
                    Component.translatable("battle_music.config.volume.illager"), (int)(cfg.illagerVolume * 100), 0, 100)
                .setDefaultValue((int)(def.illagerVolume * 100))
                .setSaveConsumer(v -> cfg.illagerVolume = v / 100f)
                .build());

            volume.addEntry(eb.startIntSlider(
                    Component.translatable("battle_music.config.volume.raid"), (int)(cfg.raidVolume * 100), 0, 100)
                .setDefaultValue((int)(def.raidVolume * 100))
                .setSaveConsumer(v -> cfg.raidVolume = v / 100f)
                .build());

            volume.addEntry(eb.startIntSlider(
                    Component.translatable("battle_music.config.volume.nether"), (int)(cfg.netherVolume * 100), 0, 100)
                .setDefaultValue((int)(def.netherVolume * 100))
                .setSaveConsumer(v -> cfg.netherVolume = v / 100f)
                .build());

            volume.addEntry(eb.startIntSlider(
                    Component.translatable("battle_music.config.volume.fluid_pitch"), (int)(cfg.underwaterPitch * 100), 50, 200)
                .setDefaultValue((int)(def.underwaterPitch * 100))
                .setTooltip(Component.translatable("battle_music.config.volume.fluid_pitch.tooltip"))
                .setSaveConsumer(v -> cfg.underwaterPitch = v / 100f)
                .build());

            ConfigCategory fade = builder.getOrCreateCategory(Component.translatable("battle_music.config.category.fade"));

            fade.addEntry(eb.startBooleanToggle(
                    Component.translatable("battle_music.config.fade.use_fade"), cfg.useFade)
                .setDefaultValue(def.useFade)
                .setTooltip(Component.translatable("battle_music.config.fade.use_fade.tooltip"))
                .setSaveConsumer(v -> cfg.useFade = v)
                .build());

            fade.addEntry(eb.startIntSlider(
                    Component.translatable("battle_music.config.fade.fade_out"), cfg.fadeOutTicks, 1, 200)
                .setDefaultValue(def.fadeOutTicks)
                .setTooltip(Component.translatable("battle_music.config.fade.fade_out.tooltip"))
                .setSaveConsumer(v -> cfg.fadeOutTicks = v)
                .build());

            fade.addEntry(eb.startIntSlider(
                    Component.translatable("battle_music.config.fade.revive_fade_in"), cfg.reviveFadeInTicks, 1, 200)
                .setDefaultValue(def.reviveFadeInTicks)
                .setTooltip(Component.translatable("battle_music.config.fade.revive_fade_in.tooltip"))
                .setSaveConsumer(v -> cfg.reviveFadeInTicks = v)
                .build());

            fade.addEntry(eb.startIntSlider(
                    Component.translatable("battle_music.config.fade.ghost_duration"), cfg.ghostDurationTicks, 1, 600)
                .setDefaultValue(def.ghostDurationTicks)
                .setTooltip(Component.translatable("battle_music.config.fade.ghost_duration.tooltip"))
                .setSaveConsumer(v -> cfg.ghostDurationTicks = v)
                .build());

            return builder.build();
        };
    }
}