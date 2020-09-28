package Callisto.DependencyInjection;

import Polaris.*;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
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

    public ServiceDescriptor(Class<?> serviceClass, UnionType unionType) {
        this.serviceClass = serviceClass;
        this.unionType = unionType;
    }

    public Class<?> getServiceClass() {
        return serviceClass;
    }

    public UnionType getUnionType() {
        return unionType;
    }

    public Result<Constructor<?>> getInjectableConstructor() {
        List<Constructor<?>> constructors = Arrays.stream(serviceClass.getDeclaredConstructors())
                .filter(constructor -> constructor.isAnnotationPresent(Inject.class))
        .collect(Collectors.toList());
        if (constructors.size() > 1)
            return Result.failure("Only one constructor with annotation: " + Inject.class.getSimpleName() + " allowed in  in: " + serviceClass.getSimpleName());
        if (constructors.size() < 1)
            return Result.failure("No constructor with annotation: " + Inject.class.getSimpleName() + " provided in " + serviceClass.getSimpleName());
        return Result.success(constructors.get(0));
    }

    private enum UnionType {
        Singleton,
        Transient,
        InterfaceSingleton,
        InterfaceTransient,
    }

    public static class Singleton extends ServiceDescriptor {

        public Singleton(Class<?> serviceClass) {
            super(serviceClass, UnionType.Singleton);
        }}

     public static class Transient extends ServiceDescriptor {
         public Transient(Class<?> serviceClass) {
             super(serviceClass, UnionType.Transient);
         }
     }

    public static class InterfaceSingleton extends ServiceDescriptor {

        private Class<?> linkedInterfaceClass;

        public InterfaceSingleton(Class<?> linkedInterfaceClass, Class<?> serviceClass) {
            super(serviceClass, UnionType.InterfaceSingleton);
            this.linkedInterfaceClass = linkedInterfaceClass;
        }

        public Class<?> getLinkedInterfaceClass() {
            return linkedInterfaceClass;
        }
    }

    public static class InterfaceTransient extends ServiceDescriptor {

        private Class<?> linkedInterfaceClass;

        public InterfaceTransient(Class<?> linkedInterfaceClass, Class<?> serviceClass) {
            super(serviceClass, UnionType.InterfaceTransient);
            this.linkedInterfaceClass = linkedInterfaceClass;
        }

        public Class<?> getLinkedInterfaceClass() {
            return linkedInterfaceClass;
        }
    }

     public <TReturn> Result<TReturn> match(
         Function<Singleton, TReturn> singletonFunction,
         Function<Transient, TReturn> transientFunction,
         Function<InterfaceSingleton, TReturn> interfaceSingletonFunction,
         Function<InterfaceTransient, TReturn> interfaceTransientFunction) throws Exception {
        switch (this.getUnionType()) {
            case Singleton:
                return Result.success(singletonFunction.apply((Singleton) this));
            case Transient:
                return Result.success(transientFunction.apply((Transient) this));
            case InterfaceSingleton:
                return Result.success(interfaceSingletonFunction.apply((InterfaceSingleton) this));
            case InterfaceTransient:
                return Result.success(interfaceTransientFunction.apply((InterfaceTransient) this));
            default:
                return Result.failure(String.format("Missing case in union type %s", this.getClass().getTypeName()));
        }
     }

}
