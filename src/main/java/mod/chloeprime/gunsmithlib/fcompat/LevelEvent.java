package mod.chloeprime.gunsmithlib.fcompat;

import net.minecraft.world.level.Level;

public class LevelEvent extends Event {
    private final Level level;

    public LevelEvent(Level level) {
        this.level = level;
    }

    public Level getLevel() {
        return level;
    }

    public static class Load extends LevelEvent {
        public Load(Level level) {
            super(level);
        }
    }

    public static class Unload extends LevelEvent {
        public Unload(Level level) {
            super(level);
        }
    }
}
