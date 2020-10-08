/*
package vega;

import polaris.Result;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SimpleSerializer<T> {

    private static final String defaultSeparator = ";";
    private Class<T> tClass;

    public SimpleSerializer(Class<T> tClass) {
        this.tClass = tClass;
    }

    public Result<String> serialize(T data) {
        Optional<Constructor<?>> constructor = Arrays.stream(tClass.getConstructors()).findFirst();
        if (constructor.isPresent()) {
            Class<?>[] parameterTypes = constructor.get().getParameterTypes();
            List<Field> fields = Arrays.stream(tClass.getFields()).filter(field -> Modifier.isPublic(field.getModifiers())).collect(Collectors.toList());
            if (parameterTypes.length == fields.size()) {
               return Result.aggregate(fields.stream().map(f -> {
                    try {
                        return Result.success(f.get(data));
                    } catch (IllegalAccessException e) {
                        return Result.failure(e.getMessage());
                    }
                })).map(values -> values.stream().map(Object::toString).reduce((a, b) -> String.join(defaultSeparator, a, b)).get());
            }
            return Result.failure("wuuuu");
        }
        return Result.failure("weeee");
    }
}
*/
