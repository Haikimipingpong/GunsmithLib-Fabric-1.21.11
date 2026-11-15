package mod.chloeprime.gunsmithlib.fabric;

import mod.chloeprime.gunsmithlib.GunsmithLib;
import mod.chloeprime.gunsmithlib.fcompat.ModContainer;
import mod.chloeprime.gunsmithlib.fcompat.NeoForge;
import mod.chloeprime.gunsmithlib.network.ModNetwork;
import net.fabricmc.api.ModInitializer;

public final class GunsmithLibFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        new GunsmithLib(NeoForge.MOD_EVENT_BUS, ModContainer.INSTANCE);
        ModNetwork.init();
        mod.chloeprime.gunsmithlib.fcompat.EventRegistrar.registerAll();
        EventHooks.registerCommon();
        TaCZBridges.register();
        ModContainer.CONFIGS.forEach(spec -> spec.load());
        NeoForge.MOD_EVENT_BUS.post(new mod.chloeprime.gunsmithlib.fcompat.FMLCommonSetupEvent());
        // NeoForge 会自己发这个事件；Fabric 没有，必须手动触发，否则能量武器等能力不会注册
        NeoForge.EVENT_BUS.post(new mod.chloeprime.gunsmithlib.fcompat.RegisterCapabilitiesEvent());
    }
}
