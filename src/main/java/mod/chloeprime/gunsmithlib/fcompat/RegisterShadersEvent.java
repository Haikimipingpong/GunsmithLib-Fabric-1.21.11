package mod.chloeprime.gunsmithlib.fcompat;

import net.minecraft.resources.Identifier;

/** Shader registration moved to render pipelines in 1.21.9+; kept as a hook for the port. */
public class RegisterShadersEvent extends Event {
    public interface ShaderRegister {
        void register(Identifier id) throws java.io.IOException;
    }
}
