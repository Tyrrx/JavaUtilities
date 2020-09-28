package Callisto.Tests;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.function.Function;

/**
 * @author David Retzlaff
 * GitHub: https://github.com/Tyrrx}
 * Date: 25.09.2020, 11:39
 */

public class CompareTypesTest {

    public String var;
    public CompareTypesTest(String init) {
        var = init;
    }

    public static void main(String[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Function<String, String> f = (a) -> a;
        Class<?> a = f.getClass();
    }
}
