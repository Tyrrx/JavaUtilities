package Callisto.Tests;

import Callisto.DependencyInjection.ServiceCollection;
import Polaris.GetValueOrThrowException;
import Polaris.Result;

/**
 * @author David Retzlaff
 * GitHub: https://github.com/Tyrrx}
 * Date: 28.09.2020, 11:58
 */

public class ABTest {

    public static void main(String[] args) throws GetValueOrThrowException {

        Result<B> bResult = new ServiceCollection()
            .addTransient(AI.class, A.class)
            .addTransient(AI.class, B.class)
            .addTransient(B.class)
            .buildServiceProvider()
            .bind(serviceProvider ->
                serviceProvider.getRequiredService(B.class));
        bResult = bResult;
    }
}
