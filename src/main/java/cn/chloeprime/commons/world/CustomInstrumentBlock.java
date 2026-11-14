package cn.chloeprime.commons.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public interface CustomInstrumentBlock {
    Optional<Holder<SoundEvent>> getNoteBlockSound(Level level, BlockPos pos, BlockState state);
}
