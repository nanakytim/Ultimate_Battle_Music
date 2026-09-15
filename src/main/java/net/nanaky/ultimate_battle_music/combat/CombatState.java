package net.nanaky.ultimate_battle_music.combat;

public enum CombatState {
    NONE(0),
    OVERWORLD_NORMAL(1),
    OVERWORLD_VARIANT(2),
    NETHER(3),
    OVERWORLD_BANDIT(4),
    RAID(5),
<<<<<<< HEAD
    BOSS(6);
=======
    INVOKER(6),
    WARDEN(7),
    WITHER(8),
    ENDER_DRAGON(9);
>>>>>>> d1d3ba7 (Fixed Illager tag and added Invoker boss)

    private final int priority;
    CombatState(int priority) { this.priority = priority; }
    public int getPriority()  { return priority; }
}