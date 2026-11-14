package mod.chloeprime.gunsmithlib.client.input;

import cn.chloeprime.commons.rpc.RPC;
import cn.chloeprime.commons.rpc.RPCTarget;
import com.mojang.blaze3d.platform.InputConstants;
import com.tacz.guns.util.InputExtraCheck;
import mod.chloeprime.gunsmithlib.GunsmithLib;
import mod.chloeprime.gunsmithlib.api.util.Gunsmith;
import mod.chloeprime.gunsmithlib.common.gunpack_extension.gun.explosive.AirburstSystem;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import mod.chloeprime.gunsmithlib.fcompat.Dist;
import mod.chloeprime.gunsmithlib.fcompat.SubscribeEvent;
import mod.chloeprime.gunsmithlib.fcompat.EventBusSubscriber;
import mod.chloeprime.gunsmithlib.fcompat.InputEvent;
import mod.chloeprime.gunsmithlib.fcompat.KeyModifier;

@EventBusSubscriber(Dist.CLIENT)
public final class BallisticComputerKey {
    public static final KeyMapping KEY_MAPPING = new KeyMapping("key.%s.ballistic_computer.desc".formatted(GunsmithLib.MOD_ID), InputConstants.Type.MOUSE, InputConstants.MOUSE_BUTTON_MIDDLE, GunsmithLibInput.CATEGORY);

    @SubscribeEvent
    public static void onMousePress(InputEvent.MouseButton.Post event) {
        if (event.getAction() != InputConstants.PRESS) {
            return;
        }
        if (InputConstants.MOUSE_BUTTON_MIDDLE != event.getButton()) {
            return;
        }
        pressBallisticComputer();
    }

    @SubscribeEvent
    public static void onKeyPress(InputEvent.Key event) {
        if (event.getAction() != InputConstants.PRESS) {
            return;
        }
        if (InputConstants.MOUSE_BUTTON_MIDDLE != event.getKey()) {
            return;
        }
        pressBallisticComputer();
    }

    public static void pressBallisticComputer() {
        if (!InputExtraCheck.isInGame()) {
            return;
        }
        var player = Minecraft.getInstance().player;
        if (player == null || player.isSpectator()) {
            return;
        }
        var gun = Gunsmith.getGunInfo(player.getMainHandItem()).orElse(null);
        if (gun == null || AirburstSystem.getAirburstRangefinderMaxDistance(gun).isEmpty()) {
            return;
        }
        RPC.call(RPCTarget.toServer(), AirburstSystem::onBallisticComputerPressed, player);
    }

    private BallisticComputerKey() {
    }
}
