package Callisto.DependencyInjection;

import Polaris.GetValueOrThrowException;
import Polaris.Option;
import Polaris.Result;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

/**
 * @author David Retzlaff
 * GitHub: https://github.com/Tyrrx}
 * Date: 25.09.2020, 22:13
 */

public class ServiceProvider {

    private Hashtable<String, ServiceDescriptor> serviceDescriptors;
    private Hashtable<String, Object> singletonRepository;

    public ServiceProvider(Hashtable<String, ServiceDescriptor> serviceDescriptorHashtable) {
        this.serviceDescriptors = serviceDescriptorHashtable;
        this.singletonRepository = new Hashtable<>();
    }

    public <T> Result<T> getRequiredService(Class<T> serviceClass) {
        return getServiceDescriptorByClass(serviceClass)
            .bind(this::resolveFromServiceDescriptor)
            .bind(this::cast);
    }

    @SuppressWarnings("unchecked")
    private <T> Result<T> cast(Object service) {
        try {
            return Result.success((T) service);
        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }
    }

    private Result<Object> resolveFromServiceDescriptor(ServiceDescriptor serviceDescriptor) {
        return serviceDescriptor.matchLifetimeDescriptor(
            this::resolveSingleton,
            this::resolveAndInitialize);
    }

    private Result<Object> resolveSingleton(ServiceDescriptor singletonServiceDescriptor) {
        if (singletonRepository.containsKey(singletonServiceDescriptor.getLinkerTypeName())) {
            return Result.success(singletonRepository.get(singletonServiceDescriptor.getLinkerTypeName()));
        }
        return this.resolveAndInitialize(singletonServiceDescriptor)
            .map(singleton -> {
                singletonRepository.put(singletonServiceDescriptor.getLinkerTypeName(), singleton);
                return singleton;
            });
    }

    private Result<Object> resolveAndInitialize(ServiceDescriptor transientServiceDescriptor) {
        return assertHasDependencies(transientServiceDescriptor)
            .bind(hasDependencies ->
                hasDependencies.match(
                    dependencies ->
                        Result.aggregate(dependencies.stream().map(this::resolveFromServiceDescriptor)) // resolve (init) dependencies and init this
                            .bind(resolvedDependencies ->
                                createNewInstance(transientServiceDescriptor, Option.some(resolvedDependencies))),
                    () -> createNewInstance(transientServiceDescriptor, Option.none()))); // init this without dependencies
    }

    private Result<Object> createNewInstance(ServiceDescriptor serviceDescriptor, Option<List<Object>> resolvedDependencies) {
        return serviceDescriptor
            .getInjectableConstructor()
            .bind(constructor -> {
                try {
                    if (resolvedDependencies.isSome())
                        return Result.success(constructor.newInstance(resolvedDependencies.getValueOrThrow().toArray()));
                    return Result.success(constructor.newInstance());
                } catch (IllegalArgumentException | InstantiationException | IllegalAccessException | InvocationTargetException | GetValueOrThrowException e) {
                    return Result.failure(e.getMessage());
                }
            });
    }

    public Result<Option<List<ServiceDescriptor>>> assertHasDependencies(ServiceDescriptor serviceDescriptor) {
        return serviceDescriptor
            .getInjectableConstructor()
            .bind(constructor -> {
                if (constructor.getParameterTypes().length == 0) {
                    return Result.success(Option.none());
                }
                return getDependencies(constructor).map(Option::some);
            });
    }

    private Result<List<ServiceDescriptor>> getDependencies(Constructor<?> constructor) {
        return Result.aggregate(Arrays.stream(constructor.getParameterTypes())
            .map(this::getServiceDescriptorByClass));
    }

    private Result<ServiceDescriptor> getServiceDescriptorByClass(Class<?> serviceClass) {
        return Option.from(this.serviceDescriptors.getOrDefault(serviceClass.getTypeName(), null))
            .toResult(() -> String.format("%s: No registered service fond for: %s", ServiceProvider.class.getSimpleName(), serviceClass.getTypeName()));
    }
}
