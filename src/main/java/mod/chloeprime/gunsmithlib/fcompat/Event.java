package mod.chloeprime.gunsmithlib.fcompat;

/** Stand-in for net.neoforged.bus.api.Event. */
public class Event implements ICancellableEvent {
    private boolean canceled;

    public boolean isCancelable() {
        return this instanceof ICancellableEvent;
    }

    public boolean isCanceled() {
        return canceled && isCancelable();
    }

    public void setCanceled(boolean canceled) {
        if (!isCancelable()) throw new UnsupportedOperationException(getClass().getName() + " is not cancelable");
        this.canceled = canceled;
    }

    public boolean canCancel() {
        return isCancelable();
    }
}
