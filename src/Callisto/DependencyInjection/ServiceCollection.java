package Callisto.DependencyInjection;

import Polaris.Option;

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

    public <T> ServiceCollection addSingleton(Class<T> serviceClass) {
        registeredServices.add(new ServiceDescriptor.Singleton(Option.none(), serviceClass));
        return this;
    }

    public <T> ServiceCollection addTransient(Class<T> serviceClass) {
        registeredServices.add(new ServiceDescriptor.Transient(Option.none(), serviceClass));
        return this;
    }

    public ServiceProvider buildServiceProvider() {
        Hashtable<String, ServiceDescriptor> serviceDescriptorHashtable = new Hashtable<>();
        registeredServices.stream().forEach(serviceDescriptor -> {
            serviceDescriptor.getLinkedInterfaceClass().match(
                    some -> serviceDescriptorHashtable.put(some.getTypeName(), serviceDescriptor),
                    () -> {
                        serviceDescriptorHashtable.put(serviceDescriptor.getServiceClass().getTypeName(), serviceDescriptor);
                    });
        });
        return new ServiceProvider(serviceDescriptorHashtable);
    }
}
