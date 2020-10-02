package Callisto.DependencyInjection;

import Polaris.GetErrorOrThrowException;
import Polaris.GetValueOrThrowException;
import Polaris.Option;
import Polaris.Result;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author David Retzlaff
 * GitHub: https://github.com/Tyrrx}
 * Date: 25.09.2020, 22:13
 */

public class ServiceProvider implements IServiceProvider{

    private Hashtable<String, ServiceDescriptor> serviceDescriptors;
    private Hashtable<String, Object> singletonRepository;
    private LinkedHashSet<ServiceDescriptor> resolvedServiceDescriptors;

    public ServiceProvider(Hashtable<String, ServiceDescriptor> serviceDescriptorHashtable) {
        this.serviceDescriptors = serviceDescriptorHashtable;
        this.singletonRepository = new Hashtable<>();
        this.resolvedServiceDescriptors = new LinkedHashSet<>();
    }

    public <T> Result<T> getRequiredService(Class<T> serviceClass) {
        resolvedServiceDescriptors.clear();
        return getServiceDescriptorByClass(serviceClass)
            .bind(this::resolveFromServiceDescriptor)
            .bind(this::cast);
    }

    public <T> T getRequiredServiceOrThrow(Class<T> serviceClass) throws GetValueOrThrowException, GetErrorOrThrowException, GetRequiredServiceOrThrowException {
        resolvedServiceDescriptors.clear();
        Result<T> serviceResult = this.getServiceDescriptorByClass(serviceClass)
            .bind(this::resolveFromServiceDescriptor)
            .bind(this::cast);
        if (serviceResult.isSuccess()) {
            return serviceResult.getValueOrThrow();
        }
        throw new GetRequiredServiceOrThrowException(serviceResult.getErrorOrThrow());
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
        if (!resolvedServiceDescriptors.add(serviceDescriptor)) {
            return createCircularDependencyError(serviceDescriptor);
        }
        return serviceDescriptor.matchLifetimeDescriptor(
            this::resolveSingleton,
            this::resolveAndInitialize);
    }

    private Result<Object> createCircularDependencyError(ServiceDescriptor serviceDescriptor) {
        String circleMessage = resolvedServiceDescriptors.stream()
            .dropWhile(s ->
                !s.equals(serviceDescriptor))
            .map(s ->
                String.format("'%s' -> '%s'",
                    s.getLinkerTypeName(),
                    s.getServiceClass().getTypeName()))
            .collect(Collectors.joining(" => "));
        return Result.failure(String.format("%s: Circular dependency at: %s => '%s' -> '%s'",
            ServiceProvider.class.getSimpleName(),
            circleMessage,
            serviceDescriptor.getLinkerTypeName(),
            serviceDescriptor.getServiceClass().getTypeName()));
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

    private Result<Option<List<ServiceDescriptor>>> assertHasDependencies(ServiceDescriptor serviceDescriptor) {
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
