package mod.chloeprime.gunsmithlib.fcompat;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Registers every {@link EventBusSubscriber}-annotated class with the matching bus.
 * NeoForge does this automatically; on Fabric we scan the mod's own classes instead.
 */
public final class EventRegistrar {
    private static final Logger LOGGER = LoggerFactory.getLogger("GunsmithLib/EventRegistrar");
    private static final String ROOT_PACKAGE = "mod.chloeprime.gunsmithlib";

    public static void registerAll() {
        var loader = FabricLoader.getInstance();
        var root = loader.getModContainer("gunsmithlib")
                .flatMap(c -> c.findPath("mod/chloeprime/gunsmithlib"))
                .orElse(null);
        if (root == null) {
            LOGGER.warn("Could not locate GunsmithLib classes; event subscribers not registered");
            return;
        }
        var classNames = new ArrayList<String>();
        try (Stream<Path> paths = Files.walk(root)) {
            paths.filter(p -> p.toString().endsWith(".class"))
                    .filter(p -> !p.toString().contains("$"))
                    .filter(p -> !p.toString().contains("/fcompat/"))
                    .filter(p -> !p.toString().contains("/fabric/"))
                    .filter(p -> !p.toString().contains("/mixin/"))
                    .map(p -> root.relativize(p).toString().replace('/', '.').replace(".class", ""))
                    .map(n -> ROOT_PACKAGE + "." + n)
                    .forEach(classNames::add);
        } catch (IOException e) {
            LOGGER.warn("Failed to scan GunsmithLib classes", e);
            return;
        }
        int registered = 0;
        for (var name : classNames) {
            try {
                var type = Class.forName(name, false, EventRegistrar.class.getClassLoader());
                var ann = type.getAnnotation(EventBusSubscriber.class);
                if (ann == null) continue;
                if (!isEnvironmentAllowed(ann)) continue;
                var bus = ann.bus() == EventBusSubscriber.Bus.MOD ? NeoForge.MOD_EVENT_BUS : NeoForge.EVENT_BUS;
                bus.register(type);
                registered++;
            } catch (Throwable t) {
                LOGGER.warn("Failed to register event subscriber {}", name, t);
            }
        }
        LOGGER.info("Registered {} GunsmithLib event subscriber classes", registered);
    }

    private static boolean isEnvironmentAllowed(EventBusSubscriber ann) {
        var env = FabricLoader.getInstance().getEnvironmentType();
        for (var dist : ann.value()) {
            if (env == EnvType.CLIENT && dist == Dist.CLIENT) return true;
            if (env == EnvType.SERVER && dist == Dist.DEDICATED_SERVER) return true;
        }
        return false;
    }

    private EventRegistrar() {}
}
