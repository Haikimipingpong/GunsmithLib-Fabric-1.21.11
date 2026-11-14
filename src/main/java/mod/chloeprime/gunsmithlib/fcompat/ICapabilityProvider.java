package mod.chloeprime.gunsmithlib.fcompat;

public interface ICapabilityProvider<S, C, T> {
    T getCapability(S context, C param);
}
