package mod.chloeprime.gunsmithlib.fcompat;

public interface IKeyConflictContext {
    boolean isActive();

    boolean conflicts(IKeyConflictContext other);
}
