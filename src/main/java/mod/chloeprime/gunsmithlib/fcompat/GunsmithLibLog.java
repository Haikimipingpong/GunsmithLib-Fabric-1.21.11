package mod.chloeprime.gunsmithlib.fcompat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GunsmithLibLog {
    public static final Logger LOGGER = LoggerFactory.getLogger("GunsmithLib");

    public static void warn(String message, Object... args) {
        LOGGER.warn(message, args);
    }

    private GunsmithLibLog() {}
}
