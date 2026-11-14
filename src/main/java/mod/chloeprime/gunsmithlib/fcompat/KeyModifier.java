package mod.chloeprime.gunsmithlib.fcompat;

public enum KeyModifier {
    NONE, SHIFT, CONTROL, ALT, SUPER;

    public boolean isActive() {
        return false;
    }
}
