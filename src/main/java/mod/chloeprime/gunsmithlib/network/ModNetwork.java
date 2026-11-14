package mod.chloeprime.gunsmithlib.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public final class ModNetwork {
    public static void init() {
        PayloadTypeRegistry.playS2C().register(S2CSyncLockedTarget.TYPE, S2CSyncLockedTarget.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(C2SSwitchVariant.TYPE, C2SSwitchVariant.STREAM_CODEC);
        net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.registerGlobalReceiver(C2SSwitchVariant.TYPE,
                (payload, context) -> context.server().execute(() -> handleSwitch(context.player(), payload)));
    }

    private static void handleSwitch(net.minecraft.server.level.ServerPlayer player, C2SSwitchVariant payload) {
        var api = mod.chloeprime.gunsmithlib.common.gunpack_extension.gun.ammo_variant.AmmoVariantSystem.class;
        var gun = mod.chloeprime.gunsmithlib.api.util.Gunsmith.getGunInfo(player.getMainHandItem()).orElse(null);
        if (gun == null) return;
        try {
            switch (payload.action()) {
                case C2SSwitchVariant.ACTION_NEXT_PART ->
                        mod.chloeprime.gunsmithlib.common.gunpack_extension.gun.ammo_variant.AmmoVariantSystem
                                .switchToNextPart(gun, player);
                case C2SSwitchVariant.ACTION_SELECT_VARIANT ->
                        mod.chloeprime.gunsmithlib.common.gunpack_extension.gun.ammo_variant.AmmoVariantSystem
                                .switchToVariant(gun, payload.index(), player);
                default -> {}
            }
        } catch (Throwable t) {
            mod.chloeprime.gunsmithlib.fcompat.GunsmithLibLog.warn("Failed to switch ammo variant", t);
        }
    }

    private ModNetwork() {}
}
