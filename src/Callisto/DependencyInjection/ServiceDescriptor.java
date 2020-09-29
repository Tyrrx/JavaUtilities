package Callisto.DependencyInjection;

import Polaris.*;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author David Retzlaff
 * GitHub: https://github.com/Tyrrx}
 * Date: 25.09.2020, 22:23
 */

public abstract class ServiceDescriptor {

    private Class<?> serviceClass;
    private UnionType unionType;
    private ScopeLifetime scopeLifetime;

    public ServiceDescriptor(Class<?> serviceClass, UnionType unionType, ScopeLifetime scopeLifetime) {
        this.serviceClass = serviceClass;
        this.unionType = unionType;
        this.scopeLifetime = scopeLifetime;
    }

    public Class<?> getServiceClass() {
        return serviceClass;
    }

    public UnionType getUnionType() {
        return unionType;
    }

    public ScopeLifetime getScopeLifetime() {
        return scopeLifetime;
    }

    public abstract Class<?> getLinkerClass();

    public Result<Constructor<?>> getInjectableConstructor() {
        List<Constructor<?>> constructors = Arrays.stream(serviceClass.getDeclaredConstructors())
            .filter(constructor -> constructor.isAnnotationPresent(Inject.class))
            .collect(Collectors.toList());
        if (constructors.size() > 1)
            return Result.failure(String.format("Only one constructor with annotation: '%s' allowed in  in: '%s'", Inject.class.getSimpleName(), serviceClass.getTypeName()));
        if (constructors.size() < 1)
            return Result.failure(String.format("No constructor with annotation: '%s' provided in: '%s'", Inject.class.getSimpleName(), serviceClass.getTypeName()));
        return Result.success(constructors.get(0));
    }

    private enum UnionType {
        InstanceReference,
        InterfaceReference,
    }

    public enum ScopeLifetime {
        Singleton,
        Transient,
    }

    public static class InstanceReference extends ServiceDescriptor {

        public InstanceReference(Class<?> serviceClass, ScopeLifetime scopeLifetime) {
            super(serviceClass, UnionType.InstanceReference, scopeLifetime);
        }

        @Override
        public Class<?> getLinkerClass() {
            return getServiceClass();
        }
    }

    public static class InterfaceReference extends ServiceDescriptor {

        private Class<?> linkedInterfaceClass;

        public InterfaceReference(Class<?> linkedInterfaceClass, Class<?> serviceClass, ScopeLifetime scopeLifetime) {
            super(serviceClass, UnionType.InterfaceReference, scopeLifetime);
            this.linkedInterfaceClass = linkedInterfaceClass;
        }

        public Class<?> getLinkedInterfaceClass() {
            return linkedInterfaceClass;
        }

        @Override
        public Class<?> getLinkerClass() {
            return getLinkedInterfaceClass();
        }
    }

    public <TReturn> Result<TReturn> matchLifetimeType(
        Function<ServiceDescriptor, TReturn> singletonLifetime,
        Function<ServiceDescriptor, TReturn> transientLifetime) {
        switch (this.getScopeLifetime()) {
            case Singleton:
                return Result.success(singletonLifetime.apply(this));
            case Transient:
                return Result.success(transientLifetime.apply(this));
            default:
                return Result.failure(String.format("Missing case in matchLifetimeType for: '%s'", this.getScopeLifetime()));
        }
    }

    public <TReturn> Result<TReturn> match(
        Function<InstanceReference, TReturn> singletonFunction,
        Function<InterfaceReference, TReturn> interfaceSingletonFunction) {
        switch (this.getUnionType()) {
            case InstanceReference:
                return Result.success(singletonFunction.apply((InstanceReference) this));
            case InterfaceReference:
                return Result.success(interfaceSingletonFunction.apply((InterfaceReference) this));
            default:
                return Result.failure(String.format("Missing case in union type %s", this.getClass().getTypeName()));
        }
    }

    public Result<Unit> matchVoid(
        Consumer<InstanceReference> singletonConsumer,
        Consumer<InterfaceReference> interfaceSingletonConsumer) {
        return this.match(instanceReference ->
        {
            singletonConsumer.accept(instanceReference);
            return No.thing();
        }, interfaceReference ->
        {
            interfaceSingletonConsumer.accept(interfaceReference);
            return No.thing();
        });
    }

    @SuppressWarnings("unchecked")
    public <T> Result<T> As() {
        try {
            return Result.success((T) this);
        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }
    }
}
