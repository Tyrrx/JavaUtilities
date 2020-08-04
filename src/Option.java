import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author David Retzlaff
 * GitHub: https://github.com/Tyrrx}
 * Date: 04.08.2020, 13:47
 */

public abstract class Option<T> {
    private boolean isSome;

    public Option(boolean isSome) {
        this.isSome = isSome;
    }

    public void match(Consumer<T> some, Consumer<Void> none) {
        if (this.isSome()) {
            var s = this.toSome();
            if (s.hasNotNullValue()) {
                some.accept(s.getValue());
                return;
            }
        }
    }


    public <T1> Option<T> map(Function<T, T1> mapper) {
        return null;
    }

    public boolean isSome() {
        return isSome;
    }

    public boolean isNone() {
        return !this.isSome();
    }

    private Some<T> toSome() {
        return (Some<T>) this;
    }

    private None<T> toNone() {
        return (None<T>) this;
    }
}
