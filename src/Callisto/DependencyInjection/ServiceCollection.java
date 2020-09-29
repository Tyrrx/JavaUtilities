package Callisto.DependencyInjection;

import Polaris.Result;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * @author David Retzlaff
 * GitHub: https://github.com/Tyrrx}
 * Date: 25.09.2020, 22:16
 */

public class ServiceCollection {

    private List<ServiceDescriptor> registeredServices = new ArrayList<>();

    public <TService> ServiceCollection addSingleton(Class<TService> serviceClass) {
        registeredServices.add(new ServiceDescriptor.InstanceReference(serviceClass, ServiceDescriptor.LifetimeDescriptor.Singleton));
        return this;
    }

    public <TService> ServiceCollection addTransient(Class<TService> serviceClass) {
        registeredServices.add(new ServiceDescriptor.InstanceReference(serviceClass, ServiceDescriptor.LifetimeDescriptor.Transient));
        return this;
    }

    public <TInterface, TService> ServiceCollection addSingleton(Class<TInterface> interfaceClass, Class<TService> serviceClass) {
        registeredServices.add(new ServiceDescriptor.InterfaceReference(interfaceClass, serviceClass, ServiceDescriptor.LifetimeDescriptor.Singleton));
        return this;
    }

    public <TInterface, TService> ServiceCollection addTransient(Class<TInterface> interfaceClass, Class<TService> serviceClass) {
        registeredServices.add(new ServiceDescriptor.InterfaceReference(interfaceClass, serviceClass, ServiceDescriptor.LifetimeDescriptor.Transient));
        return this;
    }

    public Result<ServiceProvider> buildServiceProvider() {
        Hashtable<String, ServiceDescriptor> serviceDescriptorHashtable = new Hashtable<>();
        registeredServices
            .forEach(serviceDescriptor -> serviceDescriptorHashtable.put(serviceDescriptor.getLinkerClass().getTypeName(), serviceDescriptor));
        // @todo use method to put serviceDescriptor to check if already present; Aggregate those results and map the service provider
        return Result.success(new ServiceProvider(serviceDescriptorHashtable));
    }

    private void addInterfaceServiceDescriptor(ServiceDescriptor serviceDescriptor) {

    }

    private void addServiceDescriptor(ServiceDescriptor serviceDescriptor) {

    }
}
