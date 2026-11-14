package net.neoforged.fml;

/** Stub of NeoForge's LogicalSide so third-party libs (kuroutils2) can link on Fabric. */
public enum LogicalSide {
    CLIENT, SERVER;

    public boolean isClient() {
        return this == CLIENT;
    }

    public boolean isServer() {
        return this == SERVER;
    }
}
