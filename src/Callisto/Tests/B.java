package Callisto.Tests;

import Callisto.DependencyInjection.Inject;

/**
 * @author David Retzlaff
 * GitHub: https://github.com/Tyrrx}
 * Date: 28.09.2020, 11:57
 */

public class B {

    private A a;

    @Inject
    public B(A a) {
        this.a = a;
    }
}
