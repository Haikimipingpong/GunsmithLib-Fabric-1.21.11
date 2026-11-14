package mod.chloeprime.gunsmithlib.fcompat;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SubscribeEvent {
    EventPriority value() default EventPriority.NORMAL;

    EventPriority priority() default EventPriority.NORMAL;
}
