package Callisto.DependencyInjection;

/**
 * @author David Retzlaff
 * GitHub: https://github.com/Tyrrx}
 * Date: 25.09.2020, 17:39
 */

public interface ISingletonProvider {

    public Object getInstance(String typeName);

    public void registerSingletonByInstance(String typeName, Object instance);

   /* public void registerSingletonByTypeName(String typeName);*/

}
