package mod.chloeprime.gunsmithlib.fcompat;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EventBusSubscriber {
    Bus bus() default Bus.GAME;

    Dist[] value() default {Dist.CLIENT, Dist.DEDICATED_SERVER};

    String modid() default "";

    enum Bus { GAME, MOD }
}
