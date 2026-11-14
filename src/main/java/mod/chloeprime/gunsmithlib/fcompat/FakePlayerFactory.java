package mod.chloeprime.gunsmithlib.fcompat;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

/** Fabric has no fake players; returns null and callers fall back to a real source. */
public final class FakePlayerFactory {
    public static Player get(ServerLevel level, GameProfile profile) {
        return null;
    }

    private FakePlayerFactory() {}
}
