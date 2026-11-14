package mod.chloeprime.gunsmithlib.network;

import io.netty.buffer.ByteBuf;
import mod.chloeprime.gunsmithlib.GunsmithLib;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Replaces the kuroutils2 RPC call for switching ammo variants / parts,
 * which no longer links on 1.21.11.
 */
public record C2SSwitchVariant(int action, int index) implements CustomPacketPayload {
    public static final int ACTION_NEXT_PART = 0;
    public static final int ACTION_SELECT_VARIANT = 1;

    public static final Type<C2SSwitchVariant> TYPE = new Type<>(GunsmithLib.loc("switch_variant"));
    public static final StreamCodec<ByteBuf, C2SSwitchVariant> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, C2SSwitchVariant::action,
            ByteBufCodecs.VAR_INT, C2SSwitchVariant::index,
            C2SSwitchVariant::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
