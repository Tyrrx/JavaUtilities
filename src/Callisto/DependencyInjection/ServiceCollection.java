package Callisto.DependencyInjection;

import Polaris.Option;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author David Retzlaff
 * GitHub: https://github.com/Tyrrx}
 * Date: 25.09.2020, 22:16
 */

public class ServiceCollection {

    private List<ServiceDescriptor> registeredServices = new ArrayList<>();

    public <TService> ServiceCollection addSingleton(Class<TService> serviceClass) {
        registeredServices.add(new ServiceDescriptor.Singleton(serviceClass));
        return this;
    }

    public <TService> ServiceCollection addTransient(Class<TService> serviceClass) {
        registeredServices.add(new ServiceDescriptor.Transient(serviceClass));
        return this;
    }

    public <TInterface,TService> ServiceCollection addSingleton(Class<TInterface> interfaceClass, Class<TService> serviceClass) {
        registeredServices.add(new ServiceDescriptor.InterfaceSingleton(serviceClass, interfaceClass));
        return this;
    }

    public <TInterface,TService> ServiceCollection addTransient(Class<TInterface> interfaceClass, Class<TService> serviceClass) {
        registeredServices.add(new ServiceDescriptor.InterfaceTransient(serviceClass, interfaceClass));
        return this;
    }

    public ServiceProvider buildServiceProvider() {
        Hashtable<String, ServiceDescriptor> serviceDescriptorHashtable = new Hashtable<>();
        registeredServices.forEach(serviceDescriptor ->
            serviceDescriptor.getLinkedInterfaceClass().match(
                some -> serviceDescriptorHashtable.put(some.getTypeName(), serviceDescriptor),
                () -> {
                    serviceDescriptorHashtable.put(serviceDescriptor.getServiceClass().getTypeName(), serviceDescriptor);
                }));
        return new ServiceProvider(serviceDescriptorHashtable);
    }
}
