package Callisto.DependencyInjection;

import Polaris.Result;

import java.util.Hashtable;

/**
 * @author David Retzlaff
 * GitHub: https://github.com/Tyrrx}
 * Date: 25.09.2020, 17:49
 */

public class SingletonInstanceProvider implements IServiceInstanceProvider {

    private Hashtable<String, Object> instances = new Hashtable<>();

    @Override
    public Result<Object> getInstance() {
        return null;
    }

    private Result<Object> getInstance(String typeName) {
        if (instances.containsKey(typeName))
            return Result.success(instances.get(typeName));
        return Result.failure("No singleton registered for: " + typeName);
    }

    private void registerSingletonByInstance(String typeName, Object instance) {
        instances.put(typeName, instance);
    }

  /*  @Override
    public void registerSingletonByTypeName(String typeName) {
    }*/
}
