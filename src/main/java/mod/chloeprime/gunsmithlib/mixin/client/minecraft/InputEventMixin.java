package mod.chloeprime.gunsmithlib.mixin.client.minecraft;

import mod.chloeprime.gunsmithlib.fcompat.InputEvent;
import mod.chloeprime.gunsmithlib.fcompat.NeoForge;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Bridges raw vanilla input into GunsmithLib's fcompat InputEvent,
 * which NeoForge used to fire. Without it all of GunsmithLib's keybinds are dead.
 */
public final class InputEventMixin {
    @Mixin(KeyboardHandler.class)
    public abstract static class Keyboard {
        @Inject(method = "keyPress", at = @At("HEAD"))
        private void gunsmithlib$onKey(long window, int action, KeyEvent event, CallbackInfo ci) {
            NeoForge.EVENT_BUS.post(new InputEvent.Key(event.key(), event.scancode(), action, event.modifiers()));
        }
    }

    @Mixin(MouseHandler.class)
    public abstract static class Mouse {
        @Inject(method = "onButton", at = @At("HEAD"))
        private void gunsmithlib$onButton(long window, MouseButtonInfo info, int action, CallbackInfo ci) {
            NeoForge.EVENT_BUS.post(new InputEvent.MouseButton.Pre(info.button(), action, info.modifiers()));
            NeoForge.EVENT_BUS.post(new InputEvent.MouseButton.Post(info.button(), action, info.modifiers()));
        }
    }

    private InputEventMixin() {}
}
