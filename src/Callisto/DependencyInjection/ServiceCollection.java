package Callisto.DependencyInjection;

import Polaris.GetErrorOrThrowException;
import Polaris.GetValueOrThrowException;
import Polaris.No;
import Polaris.Result;
import Polaris.Unit;

import java.lang.reflect.Modifier;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * @author David Retzlaff
 * GitHub: https://github.com/Tyrrx}
 * Date: 25.09.2020, 22:16
 */

public class ServiceCollection implements IServiceCollection{

    private List<ServiceDescriptor> preRegisteredServices = new LinkedList<>();

    /**
     * Registers a service as singleton. The service will be initialized just once.
     * @param serviceClass
     * @return this ServiceCollection
     */
    public <TService> ServiceCollection addSingleton(Class<TService> serviceClass) {
        preRegisteredServices.add(new ServiceDescriptor.InstanceReference(serviceClass, ServiceDescriptor.LifetimeDescriptor.Singleton));
        return this;
    }

    /**
     * Registers a service as transient. The service will be initialized on every resolve.
     * @param serviceClass
     * @return this ServiceCollection
     */
    public <TService> ServiceCollection addTransient(Class<TService> serviceClass) {
        preRegisteredServices.add(new ServiceDescriptor.InstanceReference(serviceClass, ServiceDescriptor.LifetimeDescriptor.Transient));
        return this;
    }

    /**
     * Registers a service as singleton and allows injecting the service via an abstract class or an interface. The service will be initialized just once.
     * @param interfaceClass
     * @param serviceClass
     * @return this ServiceCollection
     */
    public <TInterface, TService> ServiceCollection addSingleton(Class<TInterface> interfaceClass, Class<TService> serviceClass) {
        preRegisteredServices.add(new ServiceDescriptor.InterfaceReference(interfaceClass, serviceClass, ServiceDescriptor.LifetimeDescriptor.Singleton));
        return this;
    }

    /**
     * Registers a service as transient and allows injecting the service via an abstract class or an interface. The service will be initialized on every resolve.
     * @param interfaceClass
     * @param serviceClass
     * @return this ServiceCollection
     */
    public <TInterface, TService> ServiceCollection addTransient(Class<TInterface> interfaceClass, Class<TService> serviceClass) {
        preRegisteredServices.add(new ServiceDescriptor.InterfaceReference(interfaceClass, serviceClass, ServiceDescriptor.LifetimeDescriptor.Transient));
        return this;
    }

    /**
     * Creates a ServiceProvider from the registered services.
     * @return Result<ServiceProvider>
     */
    public Result<ServiceProvider> buildServiceProvider() {
        Hashtable<String, ServiceDescriptor> serviceDescriptorRegistry = new Hashtable<>();
        return this.validatePreRegisteredServices()
            .bind(validatedRegisteredServices ->
                Result.aggregate(validatedRegisteredServices.stream()
                    .map(serviceDescriptor ->
                        register(serviceDescriptor, serviceDescriptorRegistry))))
            .map(e -> new ServiceProvider(serviceDescriptorRegistry));
    }

    /**
     * Creates a ServiceProvider from the registered services.
     * @return ServiceProvider
     * @throws BuildServiceCollectionException
     * @throws GetValueOrThrowException
     * @throws GetErrorOrThrowException
     */
    public ServiceProvider buildServiceProviderOrThrow() throws BuildServiceCollectionException, GetValueOrThrowException, GetErrorOrThrowException {
        Result<ServiceProvider> providerResult = this.buildServiceProvider();
        if (providerResult.isSuccess()) {
            return providerResult.getValueOrThrow();
        }
        throw new BuildServiceCollectionException(providerResult.getErrorOrThrow());
    }

    private Result<Unit> register(ServiceDescriptor serviceDescriptor, Hashtable<String, ServiceDescriptor> serviceDescriptorRegistry) {
        String linkerName = serviceDescriptor.getLinkerClass().getTypeName();
        String serviceName = serviceDescriptor.getServiceClass().getTypeName();
        Optional<ServiceDescriptor> alreadyRegistered = serviceDescriptorRegistry.values().stream()
            .filter(currentDescriptor ->
                currentDescriptor.getServiceClass().getTypeName().equals(serviceDescriptor.getServiceClass().getTypeName()))
            .findFirst();
        if (alreadyRegistered.isPresent()) {
            return Result.failure(String.format(
                "%s: Cannot initialize the link: '%s' -> '%s' because the service: '%s' is already registered by the %s",
                ServiceCollection.class.getSimpleName(),
                linkerName,
                serviceName,
                serviceName,
                formatLinkMessage(alreadyRegistered.get())));
        }
        if (serviceDescriptorRegistry.containsKey(linkerName)) {
            return Result.failure(String.format(
                "%s: Cannot register the link: '%s' -> '%s' because the linker '%s' is already used in '%s'",
                ServiceCollection.class.getSimpleName(),
                linkerName,
                serviceName,
                linkerName,
                formatLinkMessage(serviceDescriptorRegistry.get(linkerName))));
        }
        serviceDescriptorRegistry.put(linkerName, serviceDescriptor);
        return Result.success(No.thing());
    }

    private Result<List<ServiceDescriptor>> validatePreRegisteredServices() {
        return Result.aggregate(this.preRegisteredServices.stream().map(preRegisteredService ->
            assertLinkerNotEqualsService(preRegisteredService)
                .bind(this::assertServiceNotAbstract)
                .bind(this::assertServiceNotInterface)
                .bind(this::assertServiceIsAssignableFromLinker)
                .bind(this::assertInjectableConstructorIsPresentAndValid)));
    }

    private Result<ServiceDescriptor> assertInjectableConstructorIsPresentAndValid(ServiceDescriptor preRegisteredService) {
        return preRegisteredService.getInjectableConstructor().bind(constructor -> {
            if (!(Modifier.isPublic(constructor.getModifiers()) || Modifier.isProtected(constructor.getModifiers()))) { // todo check if protected causes problems
                return Result.failure(formatErrorMessage(preRegisteredService,
                    String.format("The injectable constructor of service '%s' is not accessible from outside (private).",
                        preRegisteredService.getServiceClass().getTypeName())));
            }
            return Result.success(preRegisteredService);
        });
    }

    private Result<ServiceDescriptor> assertServiceIsAssignableFromLinker(ServiceDescriptor preRegisteredService) {
        if (!preRegisteredService.getLinkerClass().isAssignableFrom(preRegisteredService.getServiceClass())) {
            return Result.failure(formatErrorMessage(preRegisteredService,
                String.format("The linker '%s' is not assignable from the service instance '%s'.",
                    preRegisteredService.getLinkerTypeName(),
                    preRegisteredService.getServiceClass().getTypeName())));
        }
        return Result.success(preRegisteredService);
    }

    private Result<ServiceDescriptor> assertServiceNotInterface(ServiceDescriptor preRegisteredService) {
        if (Modifier.isInterface(preRegisteredService.getServiceClass().getModifiers())) {
            return Result.failure(formatErrorMessage(preRegisteredService,
                String.format("The service '%s' can never be initialized because it is an interface.",
                    preRegisteredService.getServiceClass().getTypeName())));
        }
        return Result.success(preRegisteredService);
    }

    private Result<ServiceDescriptor> assertServiceNotAbstract(ServiceDescriptor preRegisteredService) {
        if (Modifier.isAbstract(preRegisteredService.getServiceClass().getModifiers())) {
            return Result.failure(formatErrorMessage(preRegisteredService,
                String.format("The service '%s' can never be initialized because it is abstract.",
                    preRegisteredService.getServiceClass().getTypeName())));
        }
        return Result.success(preRegisteredService);
    }

    private Result<ServiceDescriptor> assertLinkerNotEqualsService(ServiceDescriptor preRegisteredService) {
        if (preRegisteredService.matchUnionType(
            instanceReference -> false,
            interfaceReference -> interfaceReference.getServiceClass().getTypeName().equals(interfaceReference.getLinkerTypeName()))) {
            return Result.failure(formatErrorMessage(preRegisteredService,
                String.format("The service type '%s' must not equal the linker type '%s'.",
                    preRegisteredService.getServiceClass().getTypeName(),
                    preRegisteredService.getLinkerTypeName())));
        }
        return Result.success(preRegisteredService);
    }

    private String formatErrorMessage(ServiceDescriptor serviceDescriptor, String errorMessage) {
        return String.format("%s in Link: '%s' -> '%s' : %s",
            ServiceCollection.class.getSimpleName(),
            serviceDescriptor.getLinkerTypeName(),
            serviceDescriptor.getServiceClass().getTypeName(),
            errorMessage);
    }

    private String formatLinkMessage(ServiceDescriptor serviceDescriptor) {
        return String.format("Link: '%s' -> '%s'",
            serviceDescriptor.getLinkerTypeName(),
            serviceDescriptor.getServiceClass().getTypeName());
    }
}
