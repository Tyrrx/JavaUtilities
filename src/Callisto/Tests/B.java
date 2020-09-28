package Callisto.Tests;

import Callisto.DependencyInjection.Inject;

/**
 * @author David Retzlaff
 * GitHub: https://github.com/Tyrrx}
 * Date: 28.09.2020, 11:57
 */

public class B {

    public AI a;

    @Inject
    public B(AI a) {
        this.a = a;
    }
}
