package Callisto.DependencyInjection;

import Polaris.Create;

import java.util.ArrayList;
import java.util.List;

/**
 * @author David Retzlaff
 * GitHub: https://github.com/Tyrrx}
 * Date: 25.09.2020, 22:16
 */

public class ServiceCollection {

    private List<ServiceDescriptor> registeredServices = new ArrayList<>();

    public <T> ServiceCollection addSingleton(Class<T> serviceClass) {
        registeredServices.add(new ServiceDescriptor.Singleton(Create.none(), serviceClass));
        return this;
    }

    public <T> ServiceCollection addTransient(Class<T> serviceClass) {
        registeredServices.add(new ServiceDescriptor.Transient(Create.none(), serviceClass));
        return this;
    }

    public ServiceProvider buildServiceProvider() {
        registeredServices.stream();
        return null;
    }
}
