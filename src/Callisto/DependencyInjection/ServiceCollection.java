package Callisto.DependencyInjection;

import Polaris.No;
import Polaris.Result;
import Polaris.Unit;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Optional;

/**
 * @author David Retzlaff
 * GitHub: https://github.com/Tyrrx}
 * Date: 25.09.2020, 22:16
 */

public class ServiceCollection {

    private List<ServiceDescriptor> preRegisteredServices = new ArrayList<>();

    public <TService> ServiceCollection addSingleton(Class<TService> serviceClass) {
        preRegisteredServices.add(new ServiceDescriptor.InstanceReference(serviceClass, ServiceDescriptor.LifetimeDescriptor.Singleton));
        return this;
    }

    public <TService> ServiceCollection addTransient(Class<TService> serviceClass) {
        preRegisteredServices.add(new ServiceDescriptor.InstanceReference(serviceClass, ServiceDescriptor.LifetimeDescriptor.Transient));
        return this;
    }

    public <TInterface, TService> ServiceCollection addSingleton(Class<TInterface> interfaceClass, Class<TService> serviceClass) {
        preRegisteredServices.add(new ServiceDescriptor.InterfaceReference(interfaceClass, serviceClass, ServiceDescriptor.LifetimeDescriptor.Singleton));
        return this;
    }

    public <TInterface, TService> ServiceCollection addTransient(Class<TInterface> interfaceClass, Class<TService> serviceClass) {
        preRegisteredServices.add(new ServiceDescriptor.InterfaceReference(interfaceClass, serviceClass, ServiceDescriptor.LifetimeDescriptor.Transient));
        return this;
    }

    public Result<ServiceProvider> buildServiceProvider() {
        Hashtable<String, ServiceDescriptor> serviceDescriptorRegistry = new Hashtable<>();
        return this.validatePreRegisteredServices()
            .bind(validatedRegisteredServices ->
                Result.aggregate(validatedRegisteredServices.stream()
                    .map(serviceDescriptor ->
                        register(serviceDescriptor, serviceDescriptorRegistry))))
            .map(e -> new ServiceProvider(serviceDescriptorRegistry));
    }

    private Result<Unit> register(ServiceDescriptor serviceDescriptor, Hashtable<String, ServiceDescriptor> serviceDescriptorRegistry) {
        String linkerName = serviceDescriptor.getLinkerClass().getTypeName();
        String serviceName = serviceDescriptor.getServiceClass().getTypeName();
        Optional<ServiceDescriptor> alreadyRegistered = serviceDescriptorRegistry.values().stream()
            .filter(s ->
                s.getServiceClass().getTypeName().equals(serviceDescriptor.getServiceClass().getTypeName()))
            .findFirst();
        if (alreadyRegistered.isPresent()) {
            return Result.failure(String.format(
                "%s: Cannot initialize the link: '%s' -> '%s' because the service: '%s' is already registered by the link: '%s' -> '%s'",
                ServiceCollection.class.getSimpleName(),
                linkerName,
                serviceName,
                serviceName,
                alreadyRegistered.get().getLinkerClass().getTypeName(),
                alreadyRegistered.get().getServiceClass().getTypeName()));
        }
        if (serviceDescriptorRegistry.containsKey(linkerName)) {
            return Result.failure(String.format(
                "%s: Cannot register the link: '%s' -> '%s' because the linker '%s' is already used in link: '%s' -> '%s'",
                ServiceCollection.class.getSimpleName(),
                linkerName,
                serviceName,
                linkerName,
                serviceDescriptorRegistry.get(linkerName).getLinkerClass().getTypeName(),
                serviceDescriptorRegistry.get(linkerName).getServiceClass().getTypeName()));
        }
        serviceDescriptorRegistry.put(linkerName, serviceDescriptor);
        return Result.success(No.thing());
    }

    private Result<List<ServiceDescriptor>> validatePreRegisteredServices() {
        return Result.aggregate(this.preRegisteredServices.stream().map(registeredService ->
        {
            if (Modifier.isAbstract(registeredService.getServiceClass().getModifiers())) {
                return Result.failure(String.format(
                    "%s: The service '%s' can never be initialized because it is abstract. Check link: '%s' -> '%s'",
                    ServiceCollection.class.getSimpleName(),
                    registeredService.getServiceClass().getTypeName(),
                    registeredService.getLinkerClass().getTypeName(),
                    registeredService.getServiceClass().getTypeName()
                ));
            }
            if (Modifier.isInterface(registeredService.getServiceClass().getModifiers())) {
                return Result.failure(String.format(
                    "%s: The service '%s' can never be initialized because it is an interface. Check link: '%s' -> '%s'",
                    ServiceCollection.class.getSimpleName(),
                    registeredService.getServiceClass().getTypeName(),
                    registeredService.getLinkerClass().getTypeName(),
                    registeredService.getServiceClass().getTypeName()
                ));
            }
            if (!registeredService.getLinkerClass().isAssignableFrom(registeredService.getServiceClass())) {
                return Result.failure(String.format(
                    "%s: The linker '%s' is not assignable from the service instance '%s'",
                    ServiceCollection.class.getSimpleName(),
                    registeredService.getLinkerClass().getTypeName(),
                    registeredService.getServiceClass().getTypeName()));
            }
            return Result.success(registeredService);
        }));
    }


}
