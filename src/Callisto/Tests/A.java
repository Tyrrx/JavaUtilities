package Callisto.Tests;

import Callisto.DependencyInjection.Inject;

/**
 * @author David Retzlaff
 * GitHub: https://github.com/Tyrrx}
 * Date: 28.09.2020, 11:57
 */

public class A implements AI {

    public String str = "HI";

    @Inject
    public A() {
    }

    public A(String str) {
        this.str = str;
    }
}
