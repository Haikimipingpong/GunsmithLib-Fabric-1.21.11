package mod.chloeprime.gunsmithlib.fcompat;

import net.minecraft.world.entity.ai.attributes.RangedAttribute;

/** NeoForge's PercentageAttribute is a RangedAttribute whose value is displayed as a percentage. */
public class PercentageAttribute extends RangedAttribute {
    public PercentageAttribute(String descriptionId, double defaultValue, double min, double max) {
        super(descriptionId, defaultValue, min, max);
    }
}
