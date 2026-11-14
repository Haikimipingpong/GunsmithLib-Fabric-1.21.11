package mod.chloeprime.gunsmithlib.fcompat;

public interface ICancellableEvent {
    boolean isCanceled();

    void setCanceled(boolean canceled);

    boolean canCancel();
}
