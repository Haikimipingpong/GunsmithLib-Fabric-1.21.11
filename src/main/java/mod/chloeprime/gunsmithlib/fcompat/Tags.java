package mod.chloeprime.gunsmithlib.fcompat;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/** Common (cross-loader) tags, matching the "c:" convention NeoForge uses. */
public final class Tags {
    private Tags() {}

    private static TagKey<Block> block(String path) {
        return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("c", path));
    }

    private static TagKey<Item> item(String path) {
        return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", path));
    }

    public static final class Blocks {
        public static final TagKey<Block> STORAGE_BLOCKS = block("storage_blocks");
        public static final TagKey<Block> STORAGE_BLOCKS_GOLD = block("storage_blocks/gold");
        public static final TagKey<Block> STORAGE_BLOCKS_IRON = block("storage_blocks/iron");
        public static final TagKey<Block> STORAGE_BLOCKS_COPPER = block("storage_blocks/copper");

        private Blocks() {}
    }

    public static final class Items {
        public static final TagKey<Item> STORAGE_BLOCKS = item("storage_blocks");
        public static final TagKey<Item> STORAGE_BLOCKS_GOLD = item("storage_blocks/gold");
        public static final TagKey<Item> STORAGE_BLOCKS_IRON = item("storage_blocks/iron");
        public static final TagKey<Item> STORAGE_BLOCKS_COPPER = item("storage_blocks/copper");
        public static final TagKey<Item> GUNPOWDERS = item("gunpowders");

        private Items() {}
    }
}
