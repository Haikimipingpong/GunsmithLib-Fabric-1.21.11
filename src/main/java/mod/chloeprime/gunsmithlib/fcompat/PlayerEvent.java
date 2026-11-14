package mod.chloeprime.gunsmithlib.fcompat;

import net.minecraft.world.entity.player.Player;

public class PlayerEvent extends EntityEvent {
    public PlayerEvent(Player player) {
        super(player);
    }

    @Override
    public Player getEntity() {
        return (Player) super.getEntity();
    }

    public static class PlayerLoggedInEvent extends PlayerEvent {
        public PlayerLoggedInEvent(Player player) {
            super(player);
        }
    }

    public static class PlayerLoggedOutEvent extends PlayerEvent {
        public PlayerLoggedOutEvent(Player player) {
            super(player);
        }
    }

    public static class Clone extends PlayerEvent {
        private final Player original;

        public Clone(Player original, Player newPlayer) {
            super(newPlayer);
            this.original = original;
        }

        public Player getOriginal() {
            return original;
        }
    }
}
