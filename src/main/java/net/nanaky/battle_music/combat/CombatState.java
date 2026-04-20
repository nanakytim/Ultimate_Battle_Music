package net.nanaky.battle_music.combat;

public enum CombatState {
    NONE(0),
    OVERWORLD_NORMAL(1),
    OVERWORLD_VARIANT(2),
    NETHER(3),
    OVERWORLD_BANDIT(4),
    RAID(5),
    BOSS(6);

    private final int priority;
    CombatState(int priority) { this.priority = priority; }
    public int getPriority()  { return priority; }
}