package Callisto.DependencyInjection;

import Polaris.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author David Retzlaff
 * GitHub: https://github.com/Tyrrx}
 * Date: 25.09.2020, 22:23
 */

public abstract class ServiceDescriptor {

    private Option<Class<?>> linkedInterfaceClass;
    private Class<?> serviceClass;

    public ServiceDescriptor(Option<Class<?>> linkedInterfaceClass, Class<?> serviceClass) {
        this.linkedInterfaceClass = linkedInterfaceClass;
        this.serviceClass = serviceClass;
    }

    public Option<Class<?>> getLinkedInterfaceClass() {
        return linkedInterfaceClass;
    }

    public Class<?> getServiceClass() {
        return serviceClass;
    }

    public Result<Constructor<?>> getInjectableConstructor() {
        List<Constructor<?>> constructors = Arrays.stream(serviceClass.getDeclaredConstructors())
                .filter(constructor -> constructor.isAnnotationPresent(Inject.class))
        .collect(Collectors.toList());
        if (constructors.size() > 1)
            return Result.failure("Only one constructor in: " + serviceClass.getSimpleName() + "with annotation: " + Inject.class.getSimpleName() + " allowed!");
        if (constructors.size() < 1)
            return Result.failure("No constructor with annotation: " + Inject.class.getSimpleName() + " provided!");
        return Result.success(constructors.get(0));
    }

    public static class Singleton extends ServiceDescriptor {
        public Singleton(Option<Class<?>> linkedInterfaceClass, Class<?> serviceClass) {
            super(linkedInterfaceClass, serviceClass);
        }}

     public static class Transient extends ServiceDescriptor {
         public Transient(Option<Class<?>> linkedInterfaceClass, Class<?> serviceClass) {
             super(linkedInterfaceClass, serviceClass);
         }
     }

     public void match(Consumer<Singleton> singletonConsumer, Consumer<Transient> transientConsumer) {
        if (this instanceof Singleton) {
            singletonConsumer.accept((Singleton) this);
        } else if (this instanceof Transient) {
            transientConsumer.accept((Transient) this);
        }
     }

    public static void main(String[] args) {
        new Singleton(Option.none(), String.class);
    }
}
