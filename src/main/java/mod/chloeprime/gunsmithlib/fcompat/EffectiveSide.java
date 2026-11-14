package mod.chloeprime.gunsmithlib.fcompat;

public final class EffectiveSide {
    public static LogicalSide get() {
        return LogicalSide.current();
    }

    private EffectiveSide() {}
}
