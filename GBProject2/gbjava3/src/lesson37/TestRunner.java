package lesson37;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestRunner {
    public static void start(Class testClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        Method[] methods = testClass.getDeclaredMethods();
        Object obj = testClass.getConstructor().newInstance();

        List<Method> l1 = Stream.of(methods).filter(m -> m.isAnnotationPresent(BeforeSuite.class)).collect(Collectors.toList());
        if(l1.size() == 1) {
            try {
                l1.get(0).invoke(obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            throw new RuntimeException();
        }

        Stream.of(methods)
                .filter(m -> m.isAnnotationPresent(Test.class))
                .sorted(Comparator.comparingInt(m -> m.getDeclaredAnnotation(Test.class).priority()))
                .forEach(m -> {
                    try {
                        m.invoke(obj);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

        List<Method> l2 = Stream.of(methods).filter(m -> m.isAnnotationPresent(AfterSuite.class)).collect(Collectors.toList());
        if(l2.size() == 1) {
            try {
                l2.get(0).invoke(obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            throw new RuntimeException();
        }

    }
}
