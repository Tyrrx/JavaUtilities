package Callisto.DependencyInjection;

import Polaris.Result;

/**
 * @author David Retzlaff
 * GitHub: https://github.com/Tyrrx}
 * Date: 25.09.2020, 17:39
 */

public interface IServiceInstanceProvider {

    public Result<Object> getInstance();
}
