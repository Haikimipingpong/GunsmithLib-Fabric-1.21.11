package mod.chloeprime.gunsmithlib.fcompat;

import net.minecraft.client.KeyMapping;

import java.util.ArrayList;
import java.util.List;

public class RegisterKeyMappingsEvent extends Event {
    public static final List<KeyMapping> MAPPINGS = new ArrayList<>();

    public void register(KeyMapping mapping) {
        MAPPINGS.add(mapping);
    }
}
