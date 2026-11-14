package mod.chloeprime.gunsmithlib.client.input;

import mod.chloeprime.gunsmithlib.GunsmithLib;
import mod.chloeprime.gunsmithlib.fcompat.Dist;
import mod.chloeprime.gunsmithlib.fcompat.SubscribeEvent;
import mod.chloeprime.gunsmithlib.fcompat.EventBusSubscriber;
import mod.chloeprime.gunsmithlib.fcompat.RegisterKeyMappingsEvent;
import mod.chloeprime.gunsmithlib.fcompat.IKeyConflictContext;
import mod.chloeprime.gunsmithlib.fcompat.KeyConflictContext;

import javax.annotation.Nonnull;

@EventBusSubscriber(Dist.CLIENT)
public class GunsmithLibInput {
    public static final net.minecraft.client.KeyMapping.Category CATEGORY = new net.minecraft.client.KeyMapping.Category(GunsmithLib.loc("main"));

    public enum KeyConflictContexts implements IKeyConflictContext {
        IN_GAME_CONCURRENT {
            @Override
            public boolean isActive() {
                return KeyConflictContext.IN_GAME.isActive();
            }

            @Override
            public boolean conflicts(@Nonnull IKeyConflictContext other) {
                return false;
            }
        },

        UNIVERSAL_CONCURRENT {
            @Override
            public boolean isActive() {
                return KeyConflictContext.UNIVERSAL.isActive();
            }

            @Override
            public boolean conflicts(@Nonnull IKeyConflictContext other) {
                return false;
            }
        }
    }

    @SubscribeEvent
    public static void onRegisteringKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(BallisticComputerKey.KEY_MAPPING);
        event.register(SwitchPartOrAmmoTypeKey.KEY_MAPPING);
    }
}
