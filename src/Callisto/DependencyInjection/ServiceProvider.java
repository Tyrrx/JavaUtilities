package Callisto.DependencyInjection;

import Polaris.GetValueOrThrowException;
import Polaris.Option;
import Polaris.Result;

import java.util.Arrays;
import java.util.Hashtable;

/**
 * @author David Retzlaff
 * GitHub: https://github.com/Tyrrx}
 * Date: 25.09.2020, 22:13
 */

public class ServiceProvider {

    private Hashtable<String, ServiceDescriptor> serviceDescriptorHashtable;

    public ServiceProvider(Hashtable<String, ServiceDescriptor> serviceDescriptorHashtable) {
        this.serviceDescriptorHashtable = serviceDescriptorHashtable;
    }

    public <T> Result<T> getRequiredService(Class<T> serviceClass) {
        return new ServiceResolver(this.serviceDescriptorHashtable)
            .resolve(serviceClass)
            .map(service -> (T) service);
    }
}
