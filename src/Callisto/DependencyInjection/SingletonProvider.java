package Callisto.DependencyInjection;

import Polaris.Create;
import Polaris.Result;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;

/**
 * @author David Retzlaff
 * GitHub: https://github.com/Tyrrx}
 * Date: 25.09.2020, 17:49
 */

public class SingletonProvider implements ISingletonProvider {

    private Hashtable<String, Object> instances = new Hashtable<>();

    @Override
    public Result<Object> getInstance(String typeName) {
        if (instances.containsKey(typeName))
            return Create.success(instances.get(typeName));
        return Create.failure("No singleton registered for: " + typeName);
    }

    @Override
    public void registerSingletonByInstance(String typeName, Object instance) {
        instances.put(typeName, instance);
    }

  /*  @Override
    public void registerSingletonByTypeName(String typeName) {
    }*/
}
