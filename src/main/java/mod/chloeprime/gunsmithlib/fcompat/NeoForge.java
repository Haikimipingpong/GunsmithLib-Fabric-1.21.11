package mod.chloeprime.gunsmithlib.fcompat;

/** Stand-in for net.neoforged.neoforge.common.NeoForge: the global game bus plus the mod bus. */
public final class NeoForge {
    public static final SimpleEventBus EVENT_BUS = new SimpleEventBus();
    public static final SimpleEventBus MOD_EVENT_BUS = new SimpleEventBus();

    private NeoForge() {}

    public static <T extends Event> T post(T event) {
        return EVENT_BUS.post(event);
    }
}
