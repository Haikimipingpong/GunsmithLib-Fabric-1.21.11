package mod.chloeprime.gunsmithlib.client.eventhandler;

import com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator;
import com.tacz.guns.client.input.AimKey;
import net.minecraft.client.Minecraft;
import mod.chloeprime.gunsmithlib.fcompat.Dist;
import mod.chloeprime.gunsmithlib.fcompat.EventPriority;
import mod.chloeprime.gunsmithlib.fcompat.SubscribeEvent;
import mod.chloeprime.gunsmithlib.fcompat.EventBusSubscriber;
import mod.chloeprime.gunsmithlib.fcompat.ClientTickEvent;
import mod.chloeprime.gunsmithlib.fcompat.InputEvent;

import static com.tacz.guns.util.InputExtraCheck.isInGame;

@EventBusSubscriber(Dist.CLIENT)
public class CantAimWhenGunItemIsInCooldown {
    private static final Minecraft MC = Minecraft.getInstance();

    @SubscribeEvent
    private static void onPlayerTick(ClientTickEvent.Pre event) {
        tryCancelAiming();
    }

    @SubscribeEvent
    private static void onPlayerTick(ClientTickEvent.Post event) {
        tryCancelAiming();
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    private static void onAimPress(InputEvent.MouseButton.Post event) {
        if (isInGame() && AimKey.AIM_KEY.isDown()) {
            tryCancelAiming();
        }
    }

    private static void tryCancelAiming() {
        var player = MC.player;
        if (player == null || player.isSpectator() || !(player instanceof IClientPlayerGunOperator operator)) {
            return;
        }
        if (operator.isAim()) {
            if (player.getCooldowns().isOnCooldown(player.getMainHandItem())) {
                operator.aim(false);
            }
        }
    }
}
