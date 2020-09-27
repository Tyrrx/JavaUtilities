package Callisto.Tests;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

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
        for (Class c : CompareTypesTest.class.getConstructors()[0].getParameterTypes()) {
            System.out.println(c.getTypeName());
        }

    }
}
