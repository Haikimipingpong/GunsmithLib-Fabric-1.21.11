package mod.chloeprime.gunsmithlib.fcompat;

import java.util.ArrayList;
import java.util.List;

/** Minimal stand-in: only config registration is used by GunsmithLib. */
public final class ModContainer {
    public static final ModContainer INSTANCE = new ModContainer();
    public static final List<IConfigSpec> CONFIGS = new ArrayList<>();

    public void registerConfig(ModConfig.Type type, IConfigSpec spec) {
        CONFIGS.add(spec);
    }
}
