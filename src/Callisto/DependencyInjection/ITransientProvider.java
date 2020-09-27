package Callisto.DependencyInjection;

/**
 * @author David Retzlaff
 * GitHub: https://github.com/Tyrrx}
 * Date: 25.09.2020, 22:06
 */

public interface ITransientProvider {

    public Object createInstance(String typeName);
}
