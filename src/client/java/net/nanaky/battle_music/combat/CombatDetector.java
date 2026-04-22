package net.nanaky.battle_music.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.zombie.*;
import net.minecraft.world.entity.monster.skeleton.*;
import net.minecraft.world.entity.monster.spider.*;
import net.minecraft.world.entity.monster.illager.*;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.nanaky.battle_music.config.BattleMusicConfig;
import net.nanaky.battle_music.config.ConfigManager;
import net.nanaky.battle_music.music.MusicManager;

import java.util.*;
import java.util.function.Predicate;

public class CombatDetector {

    private static int tickCounter = 0;
    private static Set<CombatState> lastSentStates = EnumSet.noneOf(CombatState.class);
    private static final Map<CombatState, Integer> cooldowns = new EnumMap<>(CombatState.class);
    private static final int COMBAT_COOLDOWN_TICKS = 20;
    private static final TagKey<EntityType<?>> TAG_NORMAL  = tag("normal_hostiles");
    private static final TagKey<EntityType<?>> TAG_FAR     = tag("long_range");
    private static final TagKey<EntityType<?>> TAG_VARIANT = tag("variants");
    private static final TagKey<EntityType<?>> TAG_BANDIT  = tag("illagers");

    private static TagKey<EntityType<?>> tag(String path) {
        return TagKey.create(Registries.ENTITY_TYPE,
                Identifier.fromNamespaceAndPath("battle_music", path));
    }

    private static boolean isRaidActive(Minecraft mc) {
        if (mc.player == null) return false;
        try {
            java.lang.reflect.Field f = net.minecraft.client.gui.components.BossHealthOverlay.class
                    .getDeclaredField("events");
            f.setAccessible(true);
            @SuppressWarnings("unchecked")
            java.util.Map<java.util.UUID, net.minecraft.client.gui.components.LerpingBossEvent> events =
                    (java.util.Map<java.util.UUID, net.minecraft.client.gui.components.LerpingBossEvent>) f.get(mc.gui.getBossOverlay());
            for (net.minecraft.client.gui.components.LerpingBossEvent event : events.values()) {
                net.minecraft.network.chat.ComponentContents contents = event.getName().getContents();
                if (contents instanceof net.minecraft.network.chat.contents.TranslatableContents tc
                        && tc.getKey().equals("event.minecraft.raid")) {
                    return true;
                }
            }
        } catch (Exception ignored) {}
        return false;
    }

    public static void tick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.isPaused()) return;

        BattleMusicConfig cfg = ConfigManager.getInstance();
        tickCounter++;
        if (tickCounter % Math.max(1, cfg.getCheckIntervalTicks()) != 0) return;

        Set<CombatState> detected = detectAllStates(mc, cfg);
        Set<CombatState> active   = EnumSet.noneOf(CombatState.class);

        for (CombatState state : CombatState.values()) {
            if (state == CombatState.NONE) continue;
            if (detected.contains(state)) {
                cooldowns.put(state, COMBAT_COOLDOWN_TICKS);
                active.add(state);
            } else {
                int remaining = cooldowns.getOrDefault(state, 0) - cfg.getCheckIntervalTicks();
                if (remaining > 0) {
                    cooldowns.put(state, remaining);
                    active.add(state);
                } else {
                    cooldowns.remove(state);
                }
            }
        }

        if (!active.equals(lastSentStates)) {
            lastSentStates = active.isEmpty()
                    ? EnumSet.noneOf(CombatState.class)
                    : EnumSet.copyOf(active);
            MusicManager.onCombatStatesChanged(active);
        }
    }

    public static void reset() {
        cooldowns.clear();
        lastSentStates = EnumSet.noneOf(CombatState.class);
        tickCounter    = 0;
    }

    private static Set<CombatState> detectAllStates(Minecraft mc, BattleMusicConfig cfg) {
        Set<CombatState> states = EnumSet.noneOf(CombatState.class);
        Level       level       = mc.level;
        LocalPlayer player      = mc.player;
        boolean     reqTarget   = cfg.isRequireTargetingPlayer();

        if (hasThreat(player, level, cfg.getBossRadius(), false, mob -> mob instanceof EnderDragon))
            states.add(CombatState.ENDER_DRAGON);

        if (hasThreat(player, level, cfg.getBossRadius(), false, mob -> mob instanceof WitherBoss))
            states.add(CombatState.WITHER);

        if (hasThreat(player, level, cfg.getBossRadius(), false, mob -> mob instanceof Warden))
            states.add(CombatState.WARDEN);

        if (isRaidActive(mc))
            states.add(CombatState.RAID);

        if (level.dimension().equals(Level.NETHER)) {
            if (hasThreat(player, level, cfg.getBanditRadius(), reqTarget, CombatDetector::isBandit)
                || hasThreat(player, level, cfg.getVariantRadius(), reqTarget, CombatDetector::isVariant)
                || hasThreat(player, level, cfg.getNormalRadius(), reqTarget, CombatDetector::isNormal)
                || hasThreat(player, level, cfg.getFarRadius(), false, CombatDetector::isFar)
                || hasThreat(player, level, cfg.getCreeperRadius(), reqTarget, mob -> mob instanceof Creeper))
                states.add(CombatState.NETHER);
            return states;
        }

        if (!level.dimension().equals(Level.OVERWORLD)) return states;

        if (hasThreat(player, level, cfg.getBanditRadius(), reqTarget, CombatDetector::isBandit))
            states.add(CombatState.OVERWORLD_BANDIT);

        if (hasThreat(player, level, cfg.getVariantRadius(), reqTarget, CombatDetector::isVariant))
            states.add(CombatState.OVERWORLD_VARIANT);

        if (hasThreat(player, level, cfg.getNormalRadius(), reqTarget, CombatDetector::isNormal)
            || hasThreat(player, level, cfg.getFarRadius(), false, CombatDetector::isFar)
            || hasThreat(player, level, cfg.getCreeperRadius(), reqTarget, mob -> mob instanceof Creeper))
            states.add(CombatState.OVERWORLD_NORMAL);

        return states;
    }

    private static boolean hasThreat(LocalPlayer player, Level level, double radius,
                                     boolean requireTargeting, Predicate<Mob> filter) {
        if (radius <= 0) return false;
        AABB box = player.getBoundingBox().inflate(radius);
        List<Mob> mobs = level.getEntitiesOfClass(Mob.class, box,
                mob -> !mob.isDeadOrDying() && !mob.hasCustomName() && filter.test(mob));
        if (mobs.isEmpty()) return false;
        return !requireTargeting || mobs.stream().anyMatch(CombatDetector::isThreateningPlayer);
    }

    private static boolean isThreateningPlayer(Mob mob) {
        if (mob.isAggressive()) return true;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return false;
        if (mc.player.equals(mob.getTarget())) return true;
        return isFar(mob) || isBandit(mob) || mob instanceof Slime;
    }

    private static boolean isBandit(Mob mob) {
        if (mob.getType().builtInRegistryHolder().is(TAG_BANDIT)) return true;
        return mob instanceof Pillager || mob instanceof Vindicator || mob instanceof Ravager
                || mob instanceof Evoker || mob instanceof Vex || mob instanceof Illusioner;
    }

    private static boolean isVariant(Mob mob) {
        if (mob.getType().builtInRegistryHolder().is(TAG_VARIANT)) return true;
        return mob instanceof Husk || mob instanceof Stray || mob instanceof CaveSpider
                || mob instanceof Bogged || mob instanceof Witch || mob instanceof Slime
                || mob instanceof Breeze || mob instanceof Parched;
    }

    private static boolean isFar(Mob mob) {
        if (mob.getType().builtInRegistryHolder().is(TAG_FAR)) return true;
        return mob instanceof Blaze || mob instanceof Ghast
                || mob instanceof Guardian || mob instanceof Phantom;
    }

    private static boolean isNormal(Mob mob) {
        if (mob.getType().builtInRegistryHolder().is(TAG_NORMAL)) return true;
        if (mob instanceof EnderDragon || mob instanceof WitherBoss || mob instanceof Warden
                || isBandit(mob) || isVariant(mob) || isFar(mob)) return false;
        return mob instanceof Zombie || mob instanceof Drowned
                || mob instanceof WitherSkeleton || mob instanceof Skeleton
                || mob instanceof Spider || mob instanceof MagmaCube
                || mob instanceof Piglin || mob instanceof PiglinBrute
                || mob instanceof ZombifiedPiglin || mob instanceof ElderGuardian
                || mob instanceof Hoglin;
    }
}