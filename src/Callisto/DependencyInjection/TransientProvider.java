package Callisto.DependencyInjection;

import Polaris.Create;
import Polaris.Result;

/**
 * @author David Retzlaff
 * GitHub: https://github.com/Tyrrx}
 * Date: 25.09.2020, 22:07
 */

public class TransientProvider implements ITransientProvider {

    @Override
    public Result<Object> createInstance(String typeName) {
        try {
            return Create.success(Class.forName(typeName).getConstructors()[0].newInstance());
        } catch (Exception e) {
            return Create.failure(e.toString());
        }
    }
}
