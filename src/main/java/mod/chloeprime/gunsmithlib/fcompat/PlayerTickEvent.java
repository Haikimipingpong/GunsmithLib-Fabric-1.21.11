package mod.chloeprime.gunsmithlib.fcompat;

import net.minecraft.world.entity.player.Player;

public class PlayerTickEvent extends PlayerEvent {
    public PlayerTickEvent(Player player) {
        super(player);
    }

    public static class Pre extends PlayerTickEvent {
        public Pre(Player player) {
            super(player);
        }
    }

    public static class Post extends PlayerTickEvent {
        public Post(Player player) {
            super(player);
        }
    }
}
