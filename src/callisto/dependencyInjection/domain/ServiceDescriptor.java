package callisto.dependencyInjection.domain;

import polaris.*;

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
    private LifetimeDescriptor lifetimeDescriptor;

    public ServiceDescriptor(Class<?> serviceClass, UnionType unionType, LifetimeDescriptor lifetimeDescriptor) {
        this.serviceClass = serviceClass;
        this.unionType = unionType;
        this.lifetimeDescriptor = lifetimeDescriptor;
    }

    public Class<?> getServiceClass() {
        return serviceClass;
    }

    public UnionType getUnionType() {
        return unionType;
    }

    public LifetimeDescriptor getLifetimeDescriptor() {
        return lifetimeDescriptor;
    }

    public abstract Class<?> getLinkerClass();

    public abstract String getLinkerTypeName();

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

    public enum LifetimeDescriptor {
        Singleton,
        Transient,
    }

    public static class InstanceReference extends ServiceDescriptor {

        public InstanceReference(Class<?> serviceClass, LifetimeDescriptor lifetimeDescriptor) {
            super(serviceClass, UnionType.InstanceReference, lifetimeDescriptor);
        }

        @Override
        public Class<?> getLinkerClass() {
            return getServiceClass();
        }

        @Override
        public String getLinkerTypeName() {
            return this.getLinkerClass().getTypeName();
        }
    }

    public static class InterfaceReference extends ServiceDescriptor {

        private Class<?> linkedInterfaceClass;

        public InterfaceReference(Class<?> linkedInterfaceClass, Class<?> serviceClass, LifetimeDescriptor lifetimeDescriptor) {
            super(serviceClass, UnionType.InterfaceReference, lifetimeDescriptor);
            this.linkedInterfaceClass = linkedInterfaceClass;
        }

        public Class<?> getLinkedInterfaceClass() {
            return linkedInterfaceClass;
        }

        @Override
        public Class<?> getLinkerClass() {
            return getLinkedInterfaceClass();
        }

        @Override
        public String getLinkerTypeName() {
            return getLinkerClass().getTypeName();
        }
    }

    public <TReturn> TReturn matchLifetimeDescriptor(
        Function<ServiceDescriptor, TReturn> singletonLifetime,
        Function<ServiceDescriptor, TReturn> transientLifetime) {
        switch (this.getLifetimeDescriptor()) {
            case Singleton:
                return singletonLifetime.apply(this);
            case Transient:
                return transientLifetime.apply(this);
            default:
                throw new IllegalStateException(String.format("Missing case in matchLifetimeType for: '%s'", this.getLifetimeDescriptor()));
        }
    }

    public <TReturn> TReturn matchUnionType(
        Function<InstanceReference, TReturn> singletonFunction,
        Function<InterfaceReference, TReturn> interfaceSingletonFunction) {
        switch (this.getUnionType()) {
            case InstanceReference:
                return singletonFunction.apply((InstanceReference) this);
            case InterfaceReference:
                return interfaceSingletonFunction.apply((InterfaceReference) this);
            default:
                throw new IllegalStateException(String.format("Missing case in union type %s", this.getClass().getTypeName()));
        }
    }

    public void matchVoidUnionType(
        Consumer<InstanceReference> singletonConsumer,
        Consumer<InterfaceReference> interfaceSingletonConsumer) {
        this.matchUnionType(instanceReference ->
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
    public <T> Result<T> as() {
        try {
            return Result.success((T) this);
        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }
    }
}
