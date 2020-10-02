package Callisto.DependencyInjection;

import Polaris.GetErrorOrThrowException;
import Polaris.GetValueOrThrowException;
import Polaris.Result;

/**
 * @author David Retzlaff
 * GitHub: https://github.com/Tyrrx}
 * Date: 02.10.2020, 13:07
 */

public interface IServiceCollection {

    <TService> IServiceCollection addSingleton(Class<TService> serviceClass);

    <TService> IServiceCollection addTransient(Class<TService> serviceClass);

    <TInterface, TService> IServiceCollection addSingleton(Class<TInterface> interfaceClass, Class<TService> serviceClass);

    <TInterface, TService> IServiceCollection addTransient(Class<TInterface> interfaceClass, Class<TService> serviceClass);

    Result<IServiceProvider> buildServiceProvider();

    IServiceProvider buildServiceProviderOrThrow() throws BuildServiceCollectionException, GetValueOrThrowException, GetErrorOrThrowException;

}