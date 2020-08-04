/**
 * @author David Retzlaff
 * GitHub: https://github.com/Tyrrx}
 * Date: 04.08.2020, 13:47
 */
public class Some<T> extends Option<T> {

    private T value;

    public Some(T value) {
        super(true);
        this.value = value;
    }

    public boolean hasNotNullValue() {
        return this.value != null;
    }

    public T getValue(){
        return value;
    }
}
