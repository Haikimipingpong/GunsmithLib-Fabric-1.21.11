package mod.chloeprime.gunsmithlib.fabric;

import net.fabricmc.api.ClientModInitializer;

public final class GunsmithLibClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EventHooks.registerClient();
    }
}
