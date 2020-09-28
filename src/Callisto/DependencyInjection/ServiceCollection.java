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
        registeredServices.add(new ServiceDescriptor.Singleton(serviceClass));
        return this;
    }

    public <TService> ServiceCollection addTransient(Class<TService> serviceClass) {
        registeredServices.add(new ServiceDescriptor.Transient(serviceClass));
        return this;
    }

    public <TInterface, TService> ServiceCollection addSingleton(Class<TInterface> interfaceClass, Class<TService> serviceClass) {
        registeredServices.add(new ServiceDescriptor.InterfaceSingleton(interfaceClass, serviceClass));
        return this;
    }

    public <TInterface, TService> ServiceCollection addTransient(Class<TInterface> interfaceClass, Class<TService> serviceClass) {
        registeredServices.add(new ServiceDescriptor.InterfaceTransient(interfaceClass, serviceClass));
        return this;
    }

    public Result<ServiceProvider> buildServiceProvider() {
        Hashtable<String, ServiceDescriptor> serviceDescriptorHashtable = new Hashtable<>();
        return Result.aggregate(registeredServices.stream()
            .map(serviceDescriptor ->
                serviceDescriptor.matchVoid(
                    singleton -> serviceDescriptorHashtable.put(singleton.getServiceClass().getTypeName(), singleton),
                    aTransient -> serviceDescriptorHashtable.put(aTransient.getServiceClass().getTypeName(), aTransient),
                    interfaceSingleton -> serviceDescriptorHashtable.put(interfaceSingleton.getLinkedInterfaceClass().getTypeName(), interfaceSingleton),
                    interfaceTransient -> serviceDescriptorHashtable.put(interfaceTransient.getLinkedInterfaceClass().getTypeName(), interfaceTransient))))
        .map(e -> new ServiceProvider(serviceDescriptorHashtable));
    }

    private void addInterfaceServiceDescriptor(ServiceDescriptor serviceDescriptor) {

    }

    private void addServiceDescriptor(ServiceDescriptor serviceDescriptor) {

    }
}
