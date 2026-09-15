package net.nanaky.ultimate_battle_music.combat;

public enum CombatState {
    NONE(0),
    OVERWORLD(1),
    NETHER(3),
    BANDIT(4),
    RAID(5),
    INVOKER(6),
    WARDEN(7),
    WITHER(8),
    ENDER_DRAGON(9);

    private final int priority;
    CombatState(int priority) { this.priority = priority; }
    public int getPriority()  { return priority; }
}