package callisto.dependencyInjection.domain;

import polaris.GetErrorOrThrowException;
import polaris.GetValueOrThrowException;
import polaris.Result;

/**
 * @author David Retzlaff
 * GitHub: https://github.com/Tyrrx}
 * Date: 02.10.2020, 13:07
 */

public interface IServiceProvider {

    <T> Result<T> getRequiredService(Class<T> serviceClass);

    <T> T getRequiredServiceOrThrow(Class<T> serviceClass) throws GetValueOrThrowException, GetErrorOrThrowException, GetRequiredServiceOrThrowException;

}
