package Callisto.Tests;

import Callisto.DependencyInjection.ServiceCollection;
import Polaris.GetValueOrThrowException;

/**
 * @author David Retzlaff
 * GitHub: https://github.com/Tyrrx}
 * Date: 28.09.2020, 11:58
 */

public class ABTest {

    public static void main(String[] args) throws GetValueOrThrowException {

        B a = new ServiceCollection().addTransient(A.class).addTransient(B.class).buildServiceProvider().getRequiredService(B.class);
        a = a;
    }
}
