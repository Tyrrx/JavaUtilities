package Callisto.DependencyInjection;

import Polaris.Option;
import Polaris.Result;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

/**
 * @author David Retzlaff
 * GitHub: https://github.com/Tyrrx}
 * Date: 27.09.2020, 20:03
 */

public class ServiceResolver {

    private Hashtable<String, ServiceDescriptor> serviceDescriptorHashtable;

    public ServiceResolver(Hashtable<String, ServiceDescriptor> serviceDescriptorHashtable) {
        this.serviceDescriptorHashtable = serviceDescriptorHashtable;
    }

    public Result<Object> resolve(Class<?> serviceClass) {
        return getServiceDescriptorByClass(serviceClass)
            .bind(this::resolve);
    }

    private Result<Object> resolve(ServiceDescriptor serviceDescriptor) {
        return getDependencies(serviceDescriptor).bind(dependencyServiceDescriptors ->
        {
            if (dependencyServiceDescriptors.isEmpty()) {
                return getInstanceWithoutDependencies(serviceDescriptor);
            }
            return resolveAndInitialize(serviceDescriptor, dependencyServiceDescriptors);
        });
    }

    private Result<Object> getInstanceWithoutDependencies(ServiceDescriptor serviceDescriptor) {
        return serviceDescriptor.getInjectableConstructor().bind(constructor ->
        {
            try {
                // @todo test if singleton or transient
                return Result.success(constructor.newInstance());
            } catch (Exception e) {
                return Result.failure(e.getMessage());
            }
        });
    }

    private Result<Object> resolveAndInitialize(ServiceDescriptor serviceDescriptor, List<ServiceDescriptor> serviceDescriptors) {
        return Result.aggregate(serviceDescriptors.stream().map(this::resolve))
            .bind(resolvedInstances ->
                initializeFromServiceDescriptor(serviceDescriptor, resolvedInstances));
    }

    private Result<Object> initializeFromServiceDescriptor(ServiceDescriptor serviceDescriptor, List<?> resolvedInstances) {
        return serviceDescriptor
            .getInjectableConstructor()
            .bind(constructor ->
                initializeWithDependencies(resolvedInstances.toArray(), constructor));
    }

    private Result<Object> initializeWithDependencies(Object[] resolvedInstances, Constructor<?> constructor) {
        try {
            // @todo test if singleton or transient
            return Result.success(constructor.newInstance(resolvedInstances));
        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }
    }

    private Result<List<ServiceDescriptor>> getDependencies(ServiceDescriptor serviceDescriptor) {
        return serviceDescriptor.getInjectableConstructor().bind(constructor ->
            Result.aggregate(Arrays.stream(constructor.getParameterTypes()).map(this::getServiceDescriptorByClass)));
    }

    private Result<ServiceDescriptor> getServiceDescriptorByClass(Class<?> serviceClass) {
        return Option.from(this.serviceDescriptorHashtable.getOrDefault(serviceClass.getTypeName(), null))
            .toResult(() -> String.format("No registered service fond for: %s", serviceClass.getTypeName()));
    }
}
