package mod.chloeprime.gunsmithlib.fcompat;

public enum KeyConflictContext implements IKeyConflictContext {
    UNIVERSAL {
        @Override
        public boolean isActive() {
            return true;
        }

        @Override
        public boolean conflicts(IKeyConflictContext other) {
            return true;
        }
    },
    IN_GAME {
        @Override
        public boolean isActive() {
            return net.minecraft.client.Minecraft.getInstance().screen == null;
        }

        @Override
        public boolean conflicts(IKeyConflictContext other) {
            return other == this || other == UNIVERSAL;
        }
    },
    GUI {
        @Override
        public boolean isActive() {
            return net.minecraft.client.Minecraft.getInstance().screen != null;
        }

        @Override
        public boolean conflicts(IKeyConflictContext other) {
            return other == this || other == UNIVERSAL;
        }
    }
}
