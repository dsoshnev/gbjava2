package lesson37;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Test {
    Class<? extends Throwable> expected() default Test.None.class;

    public static class None extends Throwable {
        private None() {}
    }

    int priority() default 0;
}
