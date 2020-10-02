package callisto.dependencyInjection.domain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author David Retzlaff
 * GitHub: https://github.com/Tyrrx}
 * Date: 26.09.2020, 00:22
 */
@Target(ElementType.CONSTRUCTOR)
@Retention(RetentionPolicy.RUNTIME)
public @interface Inject {
}
